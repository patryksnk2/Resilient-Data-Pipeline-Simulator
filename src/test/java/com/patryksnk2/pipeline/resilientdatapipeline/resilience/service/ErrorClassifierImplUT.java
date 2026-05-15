package com.patryksnk2.pipeline.resilientdatapipeline.resilience.service;

import com.patryksnk2.pipeline.resilientdatapipeline.exception.DataValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(MockitoExtension.class)
class ErrorClassifierImplUT {
    @InjectMocks
    private ErrorClassifierImpl sut;

    @Test
    void should_return_false_when_exception_is_DataValidationException() {
        // given
        DataValidationException exception = new DataValidationException("not retryable");
        // when / then
        Assertions.assertThat(sut.isRetryable(exception)).isFalse();
    }

    @Test
    void should_return_true_when_exception_is_retryable() {
        // given
        RuntimeException exception = new RuntimeException("retryable");
        // when / then
        Assertions.assertThat(sut.isRetryable(exception)).isTrue();
    }
    @Test
    void should_return_false_when_cause_is_DataValidationException() {
        //given
        RuntimeException wrapped = new RuntimeException("wrapper",
                new DataValidationException("root cause"));
        // when / then
        Assertions.assertThat(sut.isRetryable(wrapped)).isFalse();
    }
}