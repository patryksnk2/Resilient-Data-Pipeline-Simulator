package com.patryksnk2.pipeline.resilientdatapipeline.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Table(name = "processing_result")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
public class ProcessingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private PipelineJob pipelineJob;

    private String stageName;
    private boolean success;
    @Column(columnDefinition = "TEXT")
    private String output;
    @Column(columnDefinition = "TEXT")
    private String error;
    private Long durationMs;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime executedAt;
}
