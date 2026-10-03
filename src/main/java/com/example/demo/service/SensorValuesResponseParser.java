package com.example.demo.service;

import com.example.demo.model.SensorValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class SensorValuesResponseParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SensorValuesResponseParser.class);
    private static final Pattern NON_FINITE_MEASUREMENT = Pattern.compile(
            "(\"(?:temperature|humidity)\"\\s*:\\s*)(?:[-+]?nan|[-+]?infinity)(?=\\s*[,}])",
            Pattern.CASE_INSENSITIVE);

    private final ObjectMapper objectMapper;

    public SensorValuesResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<SensorValues> parse(String response) {
        if (NON_FINITE_MEASUREMENT.matcher(response).find()) {
            LOGGER.warn("Ignoring sensor response containing a non-finite measurement");
            return Optional.empty();
        }

        SensorValues sensorValues = objectMapper.readValue(response, SensorValues.class);
        if (sensorValues == null
                || !isFinite(sensorValues.getTemperature())
                || !isFinite(sensorValues.getHumidity())) {
            LOGGER.warn("Ignoring sensor response with missing or non-finite measurements");
            return Optional.empty();
        }

        return Optional.of(sensorValues);
    }

    private static boolean isFinite(Double value) {
        return value != null && Double.isFinite(value);
    }
}
