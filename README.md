# Resilient Data Pipeline Simulator

A Spring Boot application demonstrating resilient, asynchronous data processing.
Clients submit data over HTTP, the request is handed off to a background pipeline,
and each processing stage is wrapped in retry, circuit breaker, rate limiter and
timeout logic — so unstable dependencies don't take the whole system down with them.

## Why this project exists

Most CRUD demos assume every downstream call succeeds. In production it doesn't:
external services time out, rate limits get hit, dependencies flap between up and
down. This project simulates that reality with a configurable unstable external
service and shows how a pipeline can stay stable around it — retrying safely,
opening a circuit when a dependency degrades, and failing fast instead of
piling up blocked threads.

## Architecture

```
POST /api/v1/records
        │
        ▼
IngestServiceImpl (@Transactional)
  · saves DataRecord + PipelineJob (status: CREATED)
  · publishes JobCreatedEvent
        │
        ▼  (after commit, on a background thread)
JobCreatedEventListener (@Async, @TransactionalEventListener AFTER_COMMIT)
        │
        ▼
PipelineServiceImpl
  · marks job PROCESSING
  · runs each Stage wrapped by ResilienceFactory + ReportingStageDecorator
  · marks job SUCCEEDED or FAILED
        │
        ▼
   ┌────────────────────┬──────────────────────────┐
   │ DataValidatorStage │ ExternalServiceStage      │
   │ (non-retryable —   │ (retryable — simulates a  │
   │  bad data is bad   │  flaky external service)  │
   │  data)             │                            │
   └────────────────────┴──────────────────────────┘
```

Each stage passing through `ResilienceFactory` is wrapped in this order:

```
RateLimiter → CircuitBreaker → Retry → Timeout → stage.execute()
```

Rate limiting happens first (cheapest check, rejects overload before any work
starts). Circuit breaker checks its own state next. Retry re-attempts transient
failures. Timeout guards against a single execution hanging too long.

## Key design decisions

**Event-driven trigger instead of a direct call.** `IngestService` never calls
`PipelineService` directly — it publishes a `JobCreatedEvent` and knows nothing
about who's listening. This keeps ingestion and processing decoupled; the
pipeline could be swapped for a message queue consumer later without touching
`IngestService` at all.

**`@TransactionalEventListener(phase = AFTER_COMMIT)`** instead of a plain
`@EventListener`. The event only fires once the ingestion transaction has
committed, guaranteeing the job actually exists in the database by the time the
pipeline goes looking for it.

**`@Async` on a dedicated thread pool**, not the default `SimpleAsyncTaskExecutor`.
A named `ThreadPoolTaskExecutor` bean reuses threads instead of spawning a new
one per job — see `AsyncConfig`.

**Dedicated exception types drive resilience decisions.** `DataValidationException`
means the data itself is wrong — retrying won't help, so `ErrorClassifier` marks
it non-retryable. `ExternalServiceException` means a dependency hiccupped —
that's exactly what retry and circuit breaker exist for.

**Resilience4j over a hand-rolled implementation.** Retry with backoff and a
correct circuit breaker state machine are easy to get subtly wrong under
concurrency. Resilience4j is battle-tested; the project's own interfaces
(`RetryDecorator`, `CircuitBreakerDecorator`, etc.) act as a thin adapter layer
around it, keeping the pipeline code decoupled from the specific library.

## Endpoints

### `POST /api/v1/records`

Submits data for processing. Returns immediately with a job id — processing
happens asynchronously in the background.

```json
// Request
{
  "source": "sensor/42",
  "payload": { "temperature": 21.5, "unit": "C" }
}
```

```json
// Response — 201 Created
{ "jobId": 17 }
```

### `GET /api/v1/jobs/{jobId}/status`

Returns the current status of a job and the result of every stage it has
passed through so far.

```json
{
  "jobId": 17,
  "status": "SUCCEEDED",
  "attempts": 1,
  "lastError": null,
  "processingResults": [
    { "stageName": "DataValidatorStage", "success": true, "error": null },
    { "stageName": "ExternalServiceStage", "success": true, "error": null }
  ]
}
```

## Seeing resilience in action

The external dependency is simulated with a configurable failure rate in
`application.properties`:

```properties
simulator.failure-rate=30          # % chance the call fails outright
simulator.slow-response-rate=20    # % chance it responds too slowly (triggers timeout)
```

Try it:

1. Set `simulator.failure-rate=80` and restart.
2. Submit 10-15 records via `POST /api/v1/records`.
3. Poll `GET /api/v1/jobs/{jobId}/status` for each — you'll see some jobs
   succeed after retries, some fail after exhausting retries, and once enough
   calls fail in the sliding window, the circuit breaker opens and later jobs
   fail immediately without even attempting the call.

## Running locally

Requires Java 21.

```bash
./gradlew bootRun
```

Runs with the `dev` profile by default — an in-memory H2 database, schema
managed by Flyway. No external database setup needed.

H2 console (if you want to inspect the data directly): `http://localhost:8080/h2-console`

## Tests

```bash
./gradlew test
```

- Unit tests mock all collaborators and isolate a single class's behaviour
  (Mockito + AssertJ).
- Integration tests (`*IT`) boot the full Spring context against H2 and verify
  the pipeline end-to-end, including status transitions and persisted
  `ProcessingResult` rows.

## Tech stack

Java 21 · Spring Boot 3 · Spring Data JPA · Resilience4j · H2 (dev/test) ·
PostgreSQL (prod-ready) · Flyway · JUnit 5 · Mockito · AssertJ · Gradle
