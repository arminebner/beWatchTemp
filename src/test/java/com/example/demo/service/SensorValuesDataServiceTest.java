package com.example.demo.service;

import com.example.demo.model.SensorValues;
import com.example.demo.repository.SensorValuesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SensorValuesDataServiceTest {
    private SensorValuesRepository repository;
    private SensorValuesDataService service;

    @BeforeEach
    void setUp() {
        repository = mock(SensorValuesRepository.class);
        service = new SensorValuesDataService(repository);
    }

    @Test
    void forwardsUtcRangeToRepository() {
        Instant startDate = Instant.parse("2026-10-24T23:00:00Z");
        Instant endDate = Instant.parse("2026-10-25T23:00:00Z");
        List<SensorValues> expected = List.of(SensorValues.builder().id(1L).build());
        when(repository.findAllByTimestampGreaterThanEqualAndTimestampLessThan(startDate, endDate))
                .thenReturn(expected);

        List<SensorValues> actual = service.getAllSensorValuesByCurrentDate(startDate, endDate);

        assertSame(expected, actual);
        verify(repository).findAllByTimestampGreaterThanEqualAndTimestampLessThan(startDate, endDate);
    }
}
