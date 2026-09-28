package com.example.demo.service;

import com.example.demo.model.SensorValues;
import com.example.demo.repository.SensorValuesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SensorValuesPollService {
    private final RestTemplate restTemplate;
    private final SensorValuesRepository sensorValuesRepository;
    private final SensorValuesSocketIoService sensorValuesSocketIoService;
    private final String sensorValuesUrl;

    // change constructor
    public SensorValuesPollService(
            RestTemplate restTemplate,
            SensorValuesRepository sensorValuesRepository,
            SensorValuesSocketIoService sensorValuesSocketIoService,
            @Value("${sensor.values.url}") String sensorValuesUrl) {
        this.restTemplate = restTemplate;
        this.sensorValuesRepository = sensorValuesRepository;
        this.sensorValuesSocketIoService = sensorValuesSocketIoService;
        this.sensorValuesUrl = sensorValuesUrl;
    }

    public SensorValues getSensorValues() {
        // TODO implement if endpoint return NaN values, return null or throw exception
        return restTemplate.getForObject(sensorValuesUrl, SensorValues.class);
    }

    public SensorValues pollAndSave() {
        SensorValues sensorValues = getSensorValues();

        if (sensorValues == null) {
            throw new IllegalStateException("Sensor endpoint returned an empty response");
        }

        return sensorValuesRepository.save(sensorValues);
    }

    public SensorValues pollAndBroadcast() {
        SensorValues sensorValues = getSensorValues();
        if (sensorValues == null) {
            throw new IllegalStateException("Sensor endpoint returned an empty response");
        }

        sensorValuesSocketIoService.broadcast(sensorValues);

        return sensorValues;
    }
}