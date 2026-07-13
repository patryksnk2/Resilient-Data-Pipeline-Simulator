package com.patryksnk2.pipeline.resilientdatapipeline.api.controller;

import com.patryksnk2.pipeline.resilientdatapipeline.api.dto.JobStatusDto;
import com.patryksnk2.pipeline.resilientdatapipeline.api.exception.GlobalExceptionHandler;
import com.patryksnk2.pipeline.resilientdatapipeline.domain.Status;
import com.patryksnk2.pipeline.resilientdatapipeline.exception.PipelineJobNotFoundException;
import com.patryksnk2.pipeline.resilientdatapipeline.processing.service.PipelineJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PipelineControllerUT {
    @InjectMocks
    PipelineController sut;
    @Mock
    PipelineJobService pipelineJobService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sut)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void when_jobId_exists_then_return_200_with_status() throws Exception {
        //given
        JobStatusDto dto = new JobStatusDto(10L, Status.CREATED, 1, null, List.of());
        when(pipelineJobService.getJobStatus(10L)).thenReturn(dto);
        //when / then
        mockMvc.perform(get("/api/v1/jobs/10/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").value(10L))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.attempts").value(1));
    }

    @Test
    void when_jobId_does_not_exist_then_return_404() throws Exception {
        // given
        when(pipelineJobService.getJobStatus(999L))
                .thenThrow(new PipelineJobNotFoundException("not found"));

        // when / then
        mockMvc.perform(get("/api/v1/jobs/999/status"))
                .andExpect(status().isNotFound());
    }
}