package com.cafemetrix.cafelab.iotmonitoring.interfaces.rest;

import com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates.IoTMonitoringData;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.CreateIoTMonitoringDataCommand;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.queries.GetIoTMonitoringDataByUserIdQuery;
import com.cafemetrix.cafelab.iotmonitoring.domain.services.IoTMonitoringCommandService;
import com.cafemetrix.cafelab.iotmonitoring.domain.services.IoTMonitoringQueryService;
import com.cafemetrix.cafelab.iotmonitoring.infrastructure.authorization.sfs.support.CurrentProfileIdResolver;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IoTMonitoringController.class)
class IoTMonitoringControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IoTMonitoringCommandService commandService;

    @MockBean
    private IoTMonitoringQueryService queryService;

    @MockBean
    private CurrentProfileIdResolver currentProfileIdResolver;

    @Test
    void shouldGetIoTMonitoringData() throws Exception {
        // Arrange
        Long userId = 1L;
        CreateIoTMonitoringDataCommand command = new CreateIoTMonitoringDataCommand(
                userId, true, true, 18.0, 24.0, 50.0, 65.0
        );
        IoTMonitoringData data = new IoTMonitoringData(command);

        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.of(userId));
        when(queryService.handle(any(GetIoTMonitoringDataByUserIdQuery.class))).thenReturn(Optional.of(data));

        // Act & Assert
        mockMvc.perform(get("/api/v1/iot-monitoring/data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.sensorConnected").value(true))
                .andExpect(jsonPath("$.minTemperature").value(18.0));
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotResolved() throws Exception {
        // Arrange
        when(currentProfileIdResolver.resolveProfileId()).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/iot-monitoring/data")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
