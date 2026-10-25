package com.example.demo.controller;

import com.example.demo.model.SensorValues;
import com.example.demo.service.SensorValuesDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SensorValuesControllerTest {
    private SensorValuesDataService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(SensorValuesDataService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new SensorValuesController(service)).build();
    }

    @Test
    void acceptsUtcRangeAndDelegatesItToService() throws Exception {
        Instant startDate = Instant.parse("2026-10-24T23:00:00Z");
        Instant endDate = Instant.parse("2026-10-25T23:00:00Z");
        SensorValues first = SensorValues.builder()
                .id(1L)
                .temperature(21.5)
                .humidity(44.2)
                .deviceName("Sensor_1")
                .build();
        SensorValues second = SensorValues.builder()
                .id(2L)
                .temperature(22.5)
                .humidity(49.7)
                .deviceName("Sensor_2")
                .build();
        when(service.getAllSensorValuesByCurrentDate(startDate, endDate))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/sensorvalues/current-date")
                        .param("startDate", "2026-10-24T23:00:00Z")
                        .param("endDate", "2026-10-25T23:00:00Z"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].temperature").value(21.5))
                .andExpect(jsonPath("$[0].deviceName").value("Sensor_1"))
                .andExpect(jsonPath("$[1].temperature").value(22.5))
                .andExpect(jsonPath("$[1].deviceName").value("Sensor_2"));

        verify(service).getAllSensorValuesByCurrentDate(startDate, endDate);
    }

    @Test
    void requiresDateRange() throws Exception {
        mockMvc.perform(get("/sensorvalues/current-date")
                        .param("startDate", "2026-10-24T23:00:00Z"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsEmptyRange() throws Exception {
        mockMvc.perform(get("/sensorvalues/current-date")
                        .param("startDate", "2026-10-25T00:00:00Z")
                        .param("endDate", "2026-10-25T00:00:00Z"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsReversedRange() throws Exception {
        mockMvc.perform(get("/sensorvalues/current-date")
                        .param("startDate", "2026-10-26T00:00:00Z")
                        .param("endDate", "2026-10-25T00:00:00Z"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsMalformedTimestamps() throws Exception {
        mockMvc.perform(get("/sensorvalues/current-date")
                        .param("startDate", "not-a-timestamp")
                        .param("endDate", "2026-10-25T00:00:00Z"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
