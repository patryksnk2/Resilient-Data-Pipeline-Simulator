package com.patryksnk2.pipeline.resilientdatapipeline.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_record")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DataRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;

    @Column(name = "raw_payload",nullable = false)
    private String rawPayload;

    @CreationTimestamp
    @Column(name = "received_at", updatable = false, nullable = false)
    private LocalDateTime receivedAt;
}