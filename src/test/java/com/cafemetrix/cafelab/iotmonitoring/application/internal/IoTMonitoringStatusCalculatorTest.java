package com.cafemetrix.cafelab.iotmonitoring.application.internal;

import com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates.IoTMonitoringData;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates.IoTMonitoringHistory;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.CreateIoTMonitoringDataCommand;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.CreateIoTMonitoringHistoryCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IoTMonitoringStatusCalculatorTest {

    @Test
    void environmentalStatusShouldBeOptimalWhenWithinRange() {
        // Arrange
        IoTMonitoringData data = createData(18.0, 24.0, 50.0, 65.0);
        IoTMonitoringHistory history = createHistory(21.0, 55.0);

        // Act
        String status = IoTMonitoringStatusCalculator.environmentalStatus(data, history);

        // Assert
        assertEquals(IoTMonitoringStatusCalculator.STATUS_OPTIMAL, status);
    }

    @Test
    void environmentalStatusShouldBeWarningWhenOutOfRange() {
        // Arrange
        IoTMonitoringData data = createData(18.0, 24.0, 50.0, 65.0);
        IoTMonitoringHistory history = createHistory(25.0, 55.0); // Temp too high

        // Act
        String status = IoTMonitoringStatusCalculator.environmentalStatus(data, history);

        // Assert
        assertEquals(IoTMonitoringStatusCalculator.STATUS_WARNING, status);
    }

    @Test
    void activeAlertsShouldReturnEmptyListWhenWithinRange() {
        // Arrange
        IoTMonitoringData data = createData(18.0, 24.0, 50.0, 65.0);
        IoTMonitoringHistory history = createHistory(21.0, 55.0);

        // Act
        List<String> alerts = IoTMonitoringStatusCalculator.activeAlerts(data, history);

        // Assert
        assertTrue(alerts.isEmpty());
    }

    @Test
    void activeAlertsShouldReturnAlertWhenTemperatureIsAboveMax() {
        // Arrange
        IoTMonitoringData data = createData(18.0, 24.0, 50.0, 65.0);
        IoTMonitoringHistory history = createHistory(30.0, 55.0);

        // Act
        List<String> alerts = IoTMonitoringStatusCalculator.activeAlerts(data, history);

        // Assert
        assertTrue(alerts.contains("IOT.ALERTS.TEMPERATURE_ABOVE_MAX"));
    }

    private IoTMonitoringData createData(double minT, double maxT, double minH, double maxH) {
        return new IoTMonitoringData(new CreateIoTMonitoringDataCommand(
                1L, true, true, minT, maxT, minH, maxH
        ));
    }

    private IoTMonitoringHistory createHistory(double t, double h) {
        return new IoTMonitoringHistory(new CreateIoTMonitoringHistoryCommand(
                1L, true, t, h, null, null
        ), null);
    }
}
