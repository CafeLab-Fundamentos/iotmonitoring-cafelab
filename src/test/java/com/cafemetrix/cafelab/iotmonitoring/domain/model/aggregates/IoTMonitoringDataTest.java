package com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates;

import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.CreateIoTMonitoringDataCommand;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.UpdateIoTMonitoringDataCommand;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IoTMonitoringDataTest {

    @Test
    void shouldCreateIoTMonitoringDataFromCommand() {
        // Arrange
        CreateIoTMonitoringDataCommand command = new CreateIoTMonitoringDataCommand(
                1L, true, false, 15.0, 25.0, 40.0, 60.0
        );

        // Act
        IoTMonitoringData data = new IoTMonitoringData(command);

        // Assert
        assertEquals(1L, data.getUserId().userId());
        assertTrue(data.isSensorConnected());
        assertFalse(data.isDehumidifierConnected());
        assertEquals(15.0, data.getMinTemperature());
        assertEquals(25.0, data.getMaxTemperature());
        assertEquals(40.0, data.getMinHumidity());
        assertEquals(60.0, data.getMaxHumidity());
    }

    @Test
    void shouldApplyUpdateFromCommand() {
        // Arrange
        CreateIoTMonitoringDataCommand createCommand = new CreateIoTMonitoringDataCommand(
                1L, true, false, 15.0, 25.0, 40.0, 60.0
        );
        IoTMonitoringData data = new IoTMonitoringData(createCommand);
        UpdateIoTMonitoringDataCommand updateCommand = new UpdateIoTMonitoringDataCommand(
                1L, 1L, false, true, 20.0, 30.0, 50.0, 70.0
        );

        // Act
        data.applyUpdate(updateCommand);

        // Assert
        assertFalse(data.isSensorConnected());
        assertTrue(data.isDehumidifierConnected());
        assertEquals(20.0, data.getMinTemperature());
        assertEquals(30.0, data.getMaxTemperature());
        assertEquals(50.0, data.getMinHumidity());
        assertEquals(70.0, data.getMaxHumidity());
    }
}
