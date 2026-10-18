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
    private final String sensor1Url;
    private final String sensor2Url;

    public SensorValuesPollService(
            RestTemplate restTemplate,
            SensorValuesResponseParser sensorValuesResponseParser,
            SensorValuesRepository sensorValuesRepository,
            SensorValuesSocketIoService sensorValuesSocketIoService,
            @Value("${sensor.values.sensor1.url}") String sensor1Url,
            @Value("${sensor.values.sensor2.url}") String sensor2Url) {
        this.restTemplate = restTemplate;
        this.sensorValuesResponseParser = sensorValuesResponseParser;
        this.sensorValuesRepository = sensorValuesRepository;
        this.sensorValuesSocketIoService = sensorValuesSocketIoService;
        this.sensor1Url = sensor1Url;
        this.sensor2Url = sensor2Url;
    }

    public SensorValues getSensorValues(String sensorUrl) {
        String response = restTemplate.getForObject(sensorUrl, String.class);
        if (response == null || response.isBlank()) {
            throw new IllegalStateException("Sensor endpoint returned an empty response");
        }

        return sensorValuesResponseParser.parse(response).orElse(null);
    }

    public SensorValues pollAndSaveSensor1() {
        SensorValues sensorValues = getSensorValues(sensor1Url);
        if (sensorValues == null) {
            return null;
        }

        return sensorValuesRepository.save(sensorValues);
    }

    public SensorValues pollAndBroadcastSensor1() {
        SensorValues sensorValues = getSensorValues(sensor1Url);
        if (sensorValues == null) {
            return null;
        }

        sensorValuesSocketIoService.broadcast(sensorValues);

        return sensorValues;
    }

    public SensorValues pollAndSaveSensor2() {
        SensorValues sensorValues = getSensorValues(sensor2Url);
        if (sensorValues == null) {
            return null;
        }

        return sensorValuesRepository.save(sensorValues);
    }

    public SensorValues pollAndBroadcastSensor2() {
        SensorValues sensorValues = getSensorValues(sensor2Url);
        if (sensorValues == null) {
            return null;
        }

        sensorValuesSocketIoService.broadcast(sensorValues);

        return sensorValues;
    }
}