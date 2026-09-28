package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorValues {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperature;
    private Double humidity;
    @Column(name = "timestamp", columnDefinition = "timestamp with time zone")
    private Instant timestamp;
    private String deviceName;
}