package com.example.demo.service;

import com.example.demo.dto.SensorValuesAverage;
import com.example.demo.model.SensorValues;
import com.example.demo.repository.SensorValuesRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class SensorValuesDataService {

    private final SensorValuesRepository sensorValuesRepository;

    public List<SensorValues> getAllSensorValuesByDeviceName(String deviceName) {
        return sensorValuesRepository.findAllByDeviceName(deviceName);
    }

    public SensorValuesAverage getAverageValuesByTimeRange(String deviceName, Instant startDate, Instant endDate) {
        return sensorValuesRepository.findAverageValuesByDeviceNameAndTimeRange(deviceName, startDate, endDate);
    }
}
