package com.example.demo.service;

import com.example.demo.model.SensorValues;
import com.example.demo.repository.SensorValuesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class SensorValuesPollServiceTest {
    private static final String SENSOR_VALUES_URL = "http://sensor/sensorvalues";

    private RestTemplate restTemplate;
    private SensorValuesRepository sensorValuesRepository;
    private SensorValuesSocketIoService socketIoService;
    private SensorValuesPollService service;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        sensorValuesRepository = mock(SensorValuesRepository.class);
        socketIoService = mock(SensorValuesSocketIoService.class);
        service = new SensorValuesPollService(
                restTemplate,
                new SensorValuesResponseParser(new ObjectMapper()),
                sensorValuesRepository,
                socketIoService,
                SENSOR_VALUES_URL);
    }

    @Test
    void skipsNonFiniteMeasurementsInsteadOfSavingOrBroadcasting() {
        when(restTemplate.getForObject(SENSOR_VALUES_URL, String.class))
                .thenReturn("{\"temperature\": nan, \"humidity\": 48.2}");

        assertNull(service.pollAndSave());
        assertNull(service.pollAndBroadcast());

        verifyNoInteractions(sensorValuesRepository, socketIoService);
    }

    @Test
    void parsesFiniteMeasurementsAndDoesNotTreatNanInsideAStringAsANumber() {
        when(restTemplate.getForObject(SENSOR_VALUES_URL, String.class))
                .thenReturn("{\"temperature\": 21.5, \"humidity\": 48.2, \"deviceName\": \"nan\"}");

        SensorValues sensorValues = service.getSensorValues();

        assertEquals(21.5, sensorValues.getTemperature());
        assertEquals(48.2, sensorValues.getHumidity());
        assertEquals("nan", sensorValues.getDeviceName());
    }
}
