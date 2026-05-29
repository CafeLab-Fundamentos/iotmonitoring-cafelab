package com.cafemetrix.cafelab.iotmonitoring.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates.IoTMonitoringData;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates.IoTMonitoringHistory;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.CreateIoTMonitoringDataCommand;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.commands.CreateIoTMonitoringHistoryCommand;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class IoTMonitoringHistoryRepositoryTest {

    @Autowired
    private IoTMonitoringHistoryRepository historyRepository;

    @Autowired
    private IoTMonitoringDataRepository dataRepository;

    @Test
    void shouldSaveAndFindLatestHistoryByUserId() {
        // Arrange
        UserId userId = new UserId(100L);
        CreateIoTMonitoringDataCommand dataCommand = new CreateIoTMonitoringDataCommand(
                userId.userId(), true, true, 18.0, 24.0, 50.0, 65.0
        );
        IoTMonitoringData data = new IoTMonitoringData(dataCommand);
        dataRepository.save(data);

        CreateIoTMonitoringHistoryCommand historyCommand = new CreateIoTMonitoringHistoryCommand(
                userId.userId(), true, 22.5, 55.0, LocalDateTime.now(), 1L
        );
        IoTMonitoringHistory history = new IoTMonitoringHistory(historyCommand, data);
        historyRepository.save(history);

        // Act
        Optional<IoTMonitoringHistory> latest = historyRepository.findFirstByIotMonitoringData_UserIdOrderByTimestampDesc(userId);

        // Assert
        assertTrue(latest.isPresent());
        assertEquals(22.5, latest.get().getTemperature());
        assertEquals(userId.userId(), latest.get().getIotMonitoringData().getUserId().userId());
    }

    @Test
    void shouldSaveAndFindHistoryByBatchId() {
        // Arrange
        UserId userId = new UserId(200L);
        CreateIoTMonitoringDataCommand dataCommand = new CreateIoTMonitoringDataCommand(
                userId.userId(), true, true, 18.0, 24.0, 50.0, 65.0
        );
        IoTMonitoringData data = new IoTMonitoringData(dataCommand);
        dataRepository.save(data);

        Long batchId = 42L;
        CreateIoTMonitoringHistoryCommand historyCommand1 = new CreateIoTMonitoringHistoryCommand(
                userId.userId(), true, 22.5, 55.0, LocalDateTime.now().minusMinutes(5), batchId
        );
        CreateIoTMonitoringHistoryCommand historyCommand2 = new CreateIoTMonitoringHistoryCommand(
                userId.userId(), true, 23.0, 56.0, LocalDateTime.now(), batchId
        );
        historyRepository.save(new IoTMonitoringHistory(historyCommand1, data));
        historyRepository.save(new IoTMonitoringHistory(historyCommand2, data));

        // Act
        var batchHistory = historyRepository.findByBatchIdOrderByTimestampAsc(batchId);

        // Assert
        assertEquals(2, batchHistory.size());
        assertEquals(22.5, batchHistory.get(0).getTemperature());
        assertEquals(23.0, batchHistory.get(1).getTemperature());
        assertEquals(batchId, batchHistory.get(0).getBatchId());
    }
}
