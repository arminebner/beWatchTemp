package com.example.demo.repository;

import com.example.demo.model.SensorValues;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "socketio.port=0")
@Transactional
class SensorValuesRepositoryIntegrationTest {
    @Autowired
    private SensorValuesRepository repository;

    @Test
    void includesStartAndExcludesEndOfTimestampRange() {
        Instant start = Instant.parse("2190-01-01T00:00:00Z");
        Instant end = Instant.parse("2190-01-02T00:00:00Z");
        Instant beforeStart = start.minusMillis(1);
        Instant middle = start.plusSeconds(12 * 60 * 60);
        Instant beforeEnd = end.minusMillis(1);

        repository.saveAllAndFlush(Set.of(
                reading(beforeStart),
                reading(start),
                reading(middle),
                reading(beforeEnd),
                reading(end)));

        var results = repository.findAllByTimestampGreaterThanEqualAndTimestampLessThan(start, end);
        Set<Instant> resultTimestamps = results.stream()
                .map(SensorValues::getTimestamp)
                .collect(Collectors.toSet());

        assertEquals(3, results.size());
        assertEquals(Set.of(start, middle, beforeEnd), resultTimestamps);
    }

    private static SensorValues reading(Instant timestamp) {
        return SensorValues.builder()
                .temperature(20.0)
                .humidity(50.0)
                .timestamp(timestamp)
                .deviceName("range-boundary-test")
                .build();
    }
}
