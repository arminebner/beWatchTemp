package com.example.demo.service;

import com.example.demo.model.SensorValues;
import com.example.demo.repository.SensorValuesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SensorValuesPollServiceTest {
    private static final String SENSOR_1_URL = "http://sensor-1/sensorvalues";
    private static final String SENSOR_2_URL = "http://sensor-2/sensorvalues";

    private RestTemplate restTemplate;
    private SensorValuesRepository sensorValuesRepository;
    private SensorValuesSocketIoService socketIoService;
    private SensorValuesPollService service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        sensorValuesRepository = mock(SensorValuesRepository.class);
        socketIoService = mock(SensorValuesSocketIoService.class);
        when(sensorValuesRepository.save(any(SensorValues.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        service = new SensorValuesPollService(
                restTemplate,
                new SensorValuesResponseParser(new ObjectMapper()),
                sensorValuesRepository,
                socketIoService,
                SENSOR_1_URL,
                SENSOR_2_URL);
    }

    @Test
    void savesCalibratedReadingsFromBothSensors() {
        when(restTemplate.getForObject(SENSOR_1_URL, String.class))
                .thenReturn("{\"temperature\": 21.5, \"humidity\": 48.2, \"deviceName\": \"Sensor_1\"}");
        when(restTemplate.getForObject(SENSOR_2_URL, String.class))
                .thenReturn("{\"temperature\": 22.5, \"humidity\": 48.2, \"deviceName\": \"Sensor_2\"}");

        SensorValues sensor1 = service.pollAndSaveSensor1();
        SensorValues sensor2 = service.pollAndSaveSensor2();

        assertEquals(44.2, sensor1.getHumidity());
        assertEquals(49.7, sensor2.getHumidity());
        assertEquals("Sensor_1", sensor1.getDeviceName());
        assertEquals("Sensor_2", sensor2.getDeviceName());
        verify(restTemplate).getForObject(SENSOR_1_URL, String.class);
        verify(restTemplate).getForObject(SENSOR_2_URL, String.class);
        verify(sensorValuesRepository, times(2)).save(any(SensorValues.class));
    }

    @Test
    void broadcastsCalibratedReadingsFromBothSensors() {
        when(restTemplate.getForObject(SENSOR_1_URL, String.class))
                .thenReturn("{\"temperature\": 21.5, \"humidity\": 48.2, \"deviceName\": \"Sensor_1\"}");
        when(restTemplate.getForObject(SENSOR_2_URL, String.class))
                .thenReturn("{\"temperature\": 22.5, \"humidity\": 48.2, \"deviceName\": \"Sensor_2\"}");

        service.pollAndBroadcastSensor1();
        service.pollAndBroadcastSensor2();

        org.mockito.ArgumentCaptor<SensorValues> readings =
                org.mockito.ArgumentCaptor.forClass(SensorValues.class);
        verify(socketIoService, times(2)).broadcast(readings.capture());
        List<SensorValues> broadcastReadings = readings.getAllValues();
        assertEquals(44.2, broadcastReadings.get(0).getHumidity());
        assertEquals(49.7, broadcastReadings.get(1).getHumidity());
        assertEquals("Sensor_1", broadcastReadings.get(0).getDeviceName());
        assertEquals("Sensor_2", broadcastReadings.get(1).getDeviceName());
    }

    @Test
    void skipsNonFiniteSensorReadingsInsteadOfSavingOrBroadcasting() {
        when(restTemplate.getForObject(SENSOR_1_URL, String.class))
                .thenReturn("{\"temperature\": nan, \"humidity\": 48.2}");

        assertNull(service.pollAndSaveSensor1());
        assertNull(service.pollAndBroadcastSensor1());

        verifyNoInteractions(sensorValuesRepository, socketIoService);
    }
}
