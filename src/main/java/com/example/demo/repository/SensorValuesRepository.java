package com.example.demo.repository;

import com.example.demo.dto.SensorValuesAverage;
import com.example.demo.model.SensorValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface SensorValuesRepository extends JpaRepository<SensorValues, Long> {

    public List<SensorValues> findAllByDeviceName(String deviceName);

    @Query("""
            SELECT new com.example.demo.dto.SensorValuesAverage(
                AVG(sv.temperature),
                AVG(sv.humidity)
            )
            FROM SensorValues sv
            WHERE sv.deviceName = :deviceName
              AND sv.timestamp BETWEEN :startDate AND :endDate
            """)
    public SensorValuesAverage findAverageValuesByDeviceNameAndTimeRange(
            @Param("deviceName") String deviceName,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}
