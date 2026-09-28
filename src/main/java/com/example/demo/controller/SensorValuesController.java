package com.example.demo.controller;

import com.example.demo.dto.SensorValuesAverage;
import com.example.demo.model.SensorValues;
import com.example.demo.service.SensorValuesDataService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@AllArgsConstructor
public class SensorValuesController {
    private final SensorValuesDataService sensorValuesDataService;

    @GetMapping("/sensorvalues/{deviceName}")
    public List<SensorValues> getSensorValues(@PathVariable String deviceName) {

        return sensorValuesDataService.getAllSensorValuesByDeviceName(deviceName);
    }

    @GetMapping("/sensorvalues/{deviceName}/average")
    public SensorValuesAverage getSensorValuesAverage(@PathVariable String deviceName, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {

        return sensorValuesDataService.getAverageValuesByTimeRange(deviceName, startDate, endDate);
    }
}