package com.cafemetrix.cafelab.monitoring.application.internal.commandservices;

import com.cafemetrix.cafelab.monitoring.application.internal.outboundservices.acl.ExternalProductionService;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import com.cafemetrix.cafelab.monitoring.infrastructure.persistence.jpa.repositories.TelemetryRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelemetryRecordCommandServiceImplTest {

    @Mock
    private TelemetryRecordRepository telemetryRepository;

    @Mock
    private ExternalProductionService externalProductionService;

    @InjectMocks
    private TelemetryRecordCommandServiceImpl commandService;

    @Test
    void shouldRejectTelemetryForUnknownCoffeeLot() {
        var command = new CreateTelemetryRecordCommand(99L, 24.0, 60.0, LocalDateTime.of(2026, 6, 15, 11, 0, 0));
        when(externalProductionService.existsCoffeeLot(99L)).thenReturn(false);

        var exception = assertThrows(IllegalArgumentException.class, () -> commandService.handle(command));

        assertEquals("Cannot record telemetry. Coffee Lot with ID 99 does not exist.", exception.getMessage());
        verify(telemetryRepository, never()).save(any());
    }

    @Test
    void shouldPersistTelemetryForExistingCoffeeLot() {
        var command = new CreateTelemetryRecordCommand(7L, 24.0, 60.0, LocalDateTime.of(2026, 6, 15, 11, 0, 0));
        when(externalProductionService.existsCoffeeLot(7L)).thenReturn(true);

        commandService.handle(command);

        var captor = ArgumentCaptor.forClass(com.cafemetrix.cafelab.monitoring.domain.model.aggregates.TelemetryRecord.class);
        verify(telemetryRepository).save(captor.capture());
        assertEquals(7L, captor.getValue().getCoffeeLotId());
        assertEquals(24.0, captor.getValue().getTemperature());
        assertEquals(60.0, captor.getValue().getHumidity());
    }
}
