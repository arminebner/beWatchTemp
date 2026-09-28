package com.example.demo.service;

import com.example.demo.model.SensorValues;
import com.corundumstudio.socketio.SocketIOServer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SensorValuesSocketIoService {
    private static final String SENSOR_VALUES_EVENT = "sensor-values";
    private final SocketIOServer socketIOServer;

    public void broadcast(SensorValues sensorValues) {
        socketIOServer.getBroadcastOperations().sendEvent(SENSOR_VALUES_EVENT, sensorValues);
    }
}
