package com.cafemetrix.cafelab.monitoring.domain.model.aggregates;

import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateTelemetryRecordCommand;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "telemetry_records")
public class TelemetryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long coffeeLotId;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double humidity;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    protected TelemetryRecord() {
    }

    public TelemetryRecord(CreateTelemetryRecordCommand command) {
        this.coffeeLotId = command.coffeeLotId();
        this.temperature = command.temperature();
        this.humidity = command.humidity();
        this.timestamp = command.timestamp();
    }

    public Long getId() {
        return id;
    }

    public Long getCoffeeLotId() {
        return coffeeLotId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
