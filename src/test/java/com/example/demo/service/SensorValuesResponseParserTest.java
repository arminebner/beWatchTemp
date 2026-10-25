package com.example.demo.service;

import com.example.demo.model.SensorValues;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensorValuesResponseParserTest {
    private SensorValuesResponseParser parser;

    @BeforeEach
    void setUp() {
        parser = new SensorValuesResponseParser(new ObjectMapper());
    }

    @Test
    void calibratesHumidityForSensorOne() {
        SensorValues values = parser.parse(
                        "{\"temperature\": 21.5, \"humidity\": 48.2, \"deviceName\": \"Sensor_1\"}")
                .orElseThrow();

        assertEquals(44.2, values.getHumidity(), 0.0001);
    }

    @Test
    void calibratesHumidityForSensorTwo() {
        SensorValues values = parser.parse(
                        "{\"temperature\": 22.5, \"humidity\": 48.2, \"deviceName\": \"Sensor_2\"}")
                .orElseThrow();

        assertEquals(49.7, values.getHumidity(), 0.0001);
    }

    @Test
    void ignoresMissingOrNonFiniteMeasurements() {
        assertFalse(parser.parse("{\"temperature\": 21.5, \"deviceName\": \"Sensor_1\"}").isPresent());
        assertFalse(parser.parse("{\"temperature\": NaN, \"humidity\": 48.2}").isPresent());
        assertFalse(parser.parse("{\"temperature\": 21.5, \"humidity\": Infinity}").isPresent());
    }

    @Test
    void returnsUncalibratedValuesForOtherDevices() {
        SensorValues values = parser.parse(
                        "{\"temperature\": 20.0, \"humidity\": 50.0, \"deviceName\": \"Other\"}")
                .orElseThrow();

        assertEquals(50.0, values.getHumidity());
        assertTrue(Double.isFinite(values.getTemperature()));
    }

    @Test
    void propagatesMalformedJson() {
        assertThrows(JacksonException.class, () -> parser.parse("{invalid json"));
    }
}
