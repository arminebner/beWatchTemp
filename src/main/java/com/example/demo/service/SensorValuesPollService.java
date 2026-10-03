package com.example.demo.service;

import com.example.demo.model.SensorValues;
import com.example.demo.repository.SensorValuesRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SensorValuesPollService {
    private final RestTemplate restTemplate;
    private final SensorValuesResponseParser sensorValuesResponseParser;
    private final SensorValuesRepository sensorValuesRepository;
    private final SensorValuesSocketIoService sensorValuesSocketIoService;
    private final String sensorValuesUrl;

    public SensorValuesPollService(
            RestTemplate restTemplate,
            SensorValuesResponseParser sensorValuesResponseParser,
            SensorValuesRepository sensorValuesRepository,
            SensorValuesSocketIoService sensorValuesSocketIoService,
            @Value("${sensor.values.url}") String sensorValuesUrl) {
        this.restTemplate = restTemplate;
        this.sensorValuesResponseParser = sensorValuesResponseParser;
        this.sensorValuesRepository = sensorValuesRepository;
        this.sensorValuesSocketIoService = sensorValuesSocketIoService;
        this.sensorValuesUrl = sensorValuesUrl;
    }

    public SensorValues getSensorValues() {
        String response = restTemplate.getForObject(sensorValuesUrl, String.class);
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Sensor endpoint returned an empty response");
        }

        return sensorValuesResponseParser.parse(response).orElse(null);
    }

    public SensorValues pollAndSave() {
        SensorValues sensorValues = getSensorValues();
        if (sensorValues == null) {
            return null;
        }

        return sensorValuesRepository.save(sensorValues);
    }

    public SensorValues pollAndBroadcast() {
        SensorValues sensorValues = getSensorValues();
        if (sensorValues == null) {
            return null;
        }

        sensorValuesSocketIoService.broadcast(sensorValues);

        return sensorValues;
    }
}