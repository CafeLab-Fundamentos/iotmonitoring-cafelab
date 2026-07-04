package com.cafemetrix.cafelab.monitoring.interfaces.rest;

import com.cafemetrix.cafelab.monitoring.domain.model.aggregates.EnvironmentThreshold;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateEnvironmentThresholdCommand;
import com.cafemetrix.cafelab.monitoring.domain.model.queries.GetEnvironmentThresholdByCoffeeLotIdQuery;
import com.cafemetrix.cafelab.monitoring.domain.services.EnvironmentThresholdCommandService;
import com.cafemetrix.cafelab.monitoring.domain.services.EnvironmentThresholdQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EnvironmentThresholdsControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EnvironmentThresholdCommandService thresholdCommandService;

    @Mock
    private EnvironmentThresholdQueryService thresholdQueryService;

    @BeforeEach
    void setUp() {
        var controller = new EnvironmentThresholdsController(thresholdCommandService, thresholdQueryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new MonitoringExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateThreshold() throws Exception {
        var threshold = new EnvironmentThreshold(new CreateEnvironmentThresholdCommand(
                7L,
                18.0,
                24.0,
                50.0,
                65.0,
                10));

        when(thresholdCommandService.handle(any(CreateEnvironmentThresholdCommand.class))).thenReturn(Optional.of(threshold));

        mockMvc.perform(post("/api/v1/environment-thresholds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "coffeeLotId": 7,
                                  "minTemperature": 18.0,
                                  "maxTemperature": 24.0,
                                  "minHumidity": 50.0,
                                  "maxHumidity": 65.0,
                                  "syncIntervalSeconds": 10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.coffeeLotId").value(7))
                .andExpect(jsonPath("$.minTemperature").value(18.0))
                .andExpect(jsonPath("$.syncIntervalSeconds").value(10));
    }

    @Test
    void shouldCreateDefaultThresholdWhenThresholdDoesNotExist() throws Exception {
        var threshold = new EnvironmentThreshold(new CreateEnvironmentThresholdCommand(
                99L,
                18.0,
                22.0,
                55.0,
                65.0,
                5));

        when(thresholdQueryService.handle(any(GetEnvironmentThresholdByCoffeeLotIdQuery.class))).thenReturn(Optional.empty());
        when(thresholdCommandService.handle(any(CreateEnvironmentThresholdCommand.class))).thenReturn(Optional.of(threshold));

        mockMvc.perform(get("/api/v1/environment-thresholds/coffee-lot/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coffeeLotId").value(99))
                .andExpect(jsonPath("$.minTemperature").value(18.0))
                .andExpect(jsonPath("$.maxTemperature").value(22.0))
                .andExpect(jsonPath("$.minHumidity").value(55.0))
                .andExpect(jsonPath("$.maxHumidity").value(65.0))
                .andExpect(jsonPath("$.syncIntervalSeconds").value(5));
    }
}
