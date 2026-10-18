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
    public void pollSensorValues1() {
        sensorValuesPollService.pollAndSaveSensor1();
    }

    @Scheduled(fixedDelayString = "${sensor.values.websocket-poll-interval:2000}")
    public void pollAndBroadcastSensorValues1() {
        sensorValuesPollService.pollAndBroadcastSensor1();
    }

    @Scheduled(fixedDelayString = "${sensor.values.poll-interval:60000}")
    public void pollSensorValues2() {
        sensorValuesPollService.pollAndSaveSensor2();
    }

    @Scheduled(fixedDelayString = "${sensor.values.websocket-poll-interval:2000}")
    public void pollAndBroadcastSensorValues2() {
        sensorValuesPollService.pollAndBroadcastSensor2();
    }
}
