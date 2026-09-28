package com.example.demo.job;

import com.example.demo.service.SensorValuesPollService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SensorValuesPollJob {
    private final SensorValuesPollService sensorValuesPollService;

    @Scheduled(fixedDelayString = "${sensor.values.poll-interval:60000}")
    public void pollSensorValues() {
        sensorValuesPollService.pollAndSave();
    }

    @Scheduled(fixedDelayString = "${sensor.values.websocket-poll-interval:2000}")
    public void pollAndBroadcastSensorValues() {
        sensorValuesPollService.pollAndBroadcast();
    }
}
