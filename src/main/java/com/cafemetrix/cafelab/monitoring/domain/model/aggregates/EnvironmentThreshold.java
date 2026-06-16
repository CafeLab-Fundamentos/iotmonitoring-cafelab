package com.cafemetrix.cafelab.monitoring.domain.model.aggregates;

import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateEnvironmentThresholdCommand;
import com.cafemetrix.cafelab.monitoring.domain.model.valueobjects.HumidityThreshold;
import com.cafemetrix.cafelab.monitoring.domain.model.valueobjects.TemperatureThreshold;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "environment_thresholds")
public class EnvironmentThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long coffeeLotId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "minTemperature", column = @Column(name = "min_temperature", nullable = false)),
            @AttributeOverride(name = "maxTemperature", column = @Column(name = "max_temperature", nullable = false))
    })
    private TemperatureThreshold temperatureThreshold;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "minHumidity", column = @Column(name = "min_humidity", nullable = false)),
            @AttributeOverride(name = "maxHumidity", column = @Column(name = "max_humidity", nullable = false))
    })
    private HumidityThreshold humidityThreshold;

    @Column
    private Integer syncIntervalSeconds;

    protected EnvironmentThreshold() {
    }

    public EnvironmentThreshold(CreateEnvironmentThresholdCommand command) {
        this.coffeeLotId = command.coffeeLotId();
        this.temperatureThreshold = new TemperatureThreshold(command.minTemperature(), command.maxTemperature());
        this.humidityThreshold = new HumidityThreshold(command.minHumidity(), command.maxHumidity());
        this.syncIntervalSeconds = command.syncIntervalSeconds();
    }

    public void updateThresholds(
            Double minTemperature,
            Double maxTemperature,
            Double minHumidity,
            Double maxHumidity,
            Integer syncIntervalSeconds) {
        this.temperatureThreshold = new TemperatureThreshold(minTemperature, maxTemperature);
        this.humidityThreshold = new HumidityThreshold(minHumidity, maxHumidity);
        this.syncIntervalSeconds = syncIntervalSeconds;
    }

    public Long getId() {
        return id;
    }

    public Long getCoffeeLotId() {
        return coffeeLotId;
    }

    public TemperatureThreshold getTemperatureThreshold() {
        return temperatureThreshold;
    }

    public HumidityThreshold getHumidityThreshold() {
        return humidityThreshold;
    }

    public Integer getSyncIntervalSeconds() {
        return syncIntervalSeconds;
    }
}
