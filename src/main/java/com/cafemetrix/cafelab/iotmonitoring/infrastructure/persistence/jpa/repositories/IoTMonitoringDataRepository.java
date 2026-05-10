package com.cafemetrix.cafelab.iotmonitoring.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.iotmonitoring.domain.model.aggregates.IoTMonitoringData;
import com.cafemetrix.cafelab.iotmonitoring.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IoTMonitoringDataRepository extends JpaRepository<IoTMonitoringData, Long> {
    Optional<IoTMonitoringData> findByUserId(UserId userId);

    Optional<IoTMonitoringData> findByIdAndUserId(Long id, UserId userId);
}
