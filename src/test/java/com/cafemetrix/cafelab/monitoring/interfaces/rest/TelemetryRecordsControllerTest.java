package com.cafemetrix.cafelab.monitoring.interfaces.rest;

import com.cafemetrix.cafelab.monitoring.domain.model.aggregates.TelemetryRecord;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import com.cafemetrix.cafelab.monitoring.domain.model.queries.GetTelemetryRecordsByCoffeeLotIdQuery;
import com.cafemetrix.cafelab.monitoring.domain.services.TelemetryRecordCommandService;
import com.cafemetrix.cafelab.monitoring.domain.services.TelemetryRecordQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TelemetryRecordsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TelemetryRecordCommandService telemetryCommandService;

    @Mock
    private TelemetryRecordQueryService telemetryQueryService;

    @BeforeEach
    void setUp() {
        var controller = new TelemetryRecordsController(telemetryCommandService, telemetryQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new MonitoringExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateTelemetryRecord() throws Exception {
        var record = new TelemetryRecord(new CreateTelemetryRecordCommand(
                7L,
                25.5,
                70.2,
                LocalDateTime.of(2026, 6, 15, 10, 30, 0)));

        when(telemetryCommandService.handle(any(CreateTelemetryRecordCommand.class))).thenReturn(Optional.of(record));

        mockMvc.perform(post("/api/v1/telemetry-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "coffeeLotId": 7,
                                  "temperature": 25.5,
                                  "humidity": 70.2,
                                  "timestamp": "2026-06-15T10:30:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coffeeLotId").value(7))
                .andExpect(jsonPath("$.temperature").value(25.5))
                .andExpect(jsonPath("$.humidity").value(70.2))
                .andExpect(jsonPath("$.timestamp[0]").value(2026))
                .andExpect(jsonPath("$.timestamp[1]").value(6))
                .andExpect(jsonPath("$.timestamp[2]").value(15))
                .andExpect(jsonPath("$.timestamp[3]").value(10))
                .andExpect(jsonPath("$.timestamp[4]").value(30));
    }

    @Test
    void shouldListTelemetryHistoryByCoffeeLotId() throws Exception {
        var first = new TelemetryRecord(new CreateTelemetryRecordCommand(
                7L,
                21.3,
                58.0,
                LocalDateTime.of(2026, 6, 15, 10, 0, 0)));
        var second = new TelemetryRecord(new CreateTelemetryRecordCommand(
                7L,
                22.0,
                60.5,
                LocalDateTime.of(2026, 6, 15, 10, 5, 0)));

        when(telemetryQueryService.handle(any(GetTelemetryRecordsByCoffeeLotIdQuery.class)))
                .thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/v1/telemetry-records/coffee-lot/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coffeeLotId").value(7))
                .andExpect(jsonPath("$[0].temperature").value(21.3))
                .andExpect(jsonPath("$[1].humidity").value(60.5));
    }
}
