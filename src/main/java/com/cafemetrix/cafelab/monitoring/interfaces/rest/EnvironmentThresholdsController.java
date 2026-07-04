package com.cafemetrix.cafelab.monitoring.interfaces.rest;

import com.cafemetrix.cafelab.monitoring.domain.exceptions.EnvironmentThresholdNotFoundException;
import com.cafemetrix.cafelab.monitoring.domain.model.aggregates.EnvironmentThreshold;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.CreateEnvironmentThresholdCommand;
import com.cafemetrix.cafelab.monitoring.domain.model.commands.UpdateEnvironmentThresholdCommand;
import com.cafemetrix.cafelab.monitoring.domain.model.queries.GetEnvironmentThresholdByCoffeeLotIdQuery;
import com.cafemetrix.cafelab.monitoring.domain.services.EnvironmentThresholdCommandService;
import com.cafemetrix.cafelab.monitoring.domain.services.EnvironmentThresholdQueryService;
import com.cafemetrix.cafelab.monitoring.interfaces.rest.resources.CreateEnvironmentThresholdResource;
import com.cafemetrix.cafelab.monitoring.interfaces.rest.resources.EnvironmentThresholdResource;
import com.cafemetrix.cafelab.monitoring.interfaces.rest.transform.EnvironmentThresholdResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/environment-thresholds", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Environment Thresholds", description = "Management endpoints for IoT sensor safe thresholds per coffee lot")
public class EnvironmentThresholdsController {

    private static final double DEFAULT_MIN_TEMPERATURE = 18.0;
    private static final double DEFAULT_MAX_TEMPERATURE = 22.0;
    private static final double DEFAULT_MIN_HUMIDITY = 55.0;
    private static final double DEFAULT_MAX_HUMIDITY = 65.0;
    private static final int DEFAULT_SYNC_INTERVAL_SECONDS = 5;

    private final EnvironmentThresholdCommandService thresholdCommandService;
    private final EnvironmentThresholdQueryService thresholdQueryService;

    public EnvironmentThresholdsController(
            EnvironmentThresholdCommandService thresholdCommandService,
            EnvironmentThresholdQueryService thresholdQueryService) {
        this.thresholdCommandService = thresholdCommandService;
        this.thresholdQueryService = thresholdQueryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnvironmentThresholdResource> createThreshold(@RequestBody CreateEnvironmentThresholdResource resource) {
        var command = new CreateEnvironmentThresholdCommand(
                resource.coffeeLotId(),
                resource.minTemperature(),
                resource.maxTemperature(),
                resource.minHumidity(),
                resource.maxHumidity(),
                resource.syncIntervalSeconds());
        var threshold = thresholdCommandService.handle(command);

        if (threshold.isEmpty()) {
            throw new IllegalStateException("No se pudo registrar la configuracion de umbrales");
        }

        return new ResponseEntity<>(
                EnvironmentThresholdResourceFromEntityAssembler.toResourceFromEntity(threshold.get()),
                HttpStatus.CREATED);
    }

    @GetMapping("/coffee-lot/{coffeeLotId}")
    public ResponseEntity<EnvironmentThresholdResource> getThresholdByCoffeeLotId(@PathVariable Long coffeeLotId) {
        var query = new GetEnvironmentThresholdByCoffeeLotIdQuery(coffeeLotId);
        var threshold = thresholdQueryService.handle(query);

        var existingOrDefault = threshold.orElseGet(() -> createDefaultThreshold(coffeeLotId));
        return ResponseEntity.ok(EnvironmentThresholdResourceFromEntityAssembler.toResourceFromEntity(existingOrDefault));
    }

    private EnvironmentThreshold createDefaultThreshold(Long coffeeLotId) {
        var command = new CreateEnvironmentThresholdCommand(
                coffeeLotId,
                DEFAULT_MIN_TEMPERATURE,
                DEFAULT_MAX_TEMPERATURE,
                DEFAULT_MIN_HUMIDITY,
                DEFAULT_MAX_HUMIDITY,
                DEFAULT_SYNC_INTERVAL_SECONDS);
        return thresholdCommandService
                .handle(command)
                .orElseThrow(() -> new IllegalStateException("No se pudo registrar la configuracion de umbrales"));
    }

    @PutMapping(value = "/coffee-lot/{coffeeLotId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnvironmentThresholdResource> updateThreshold(
            @PathVariable Long coffeeLotId,
            @RequestBody CreateEnvironmentThresholdResource resource) {
        var command = new UpdateEnvironmentThresholdCommand(
                coffeeLotId,
                resource.minTemperature(),
                resource.maxTemperature(),
                resource.minHumidity(),
                resource.maxHumidity(),
                resource.syncIntervalSeconds());
        var updatedThreshold = thresholdCommandService.handle(command);

        if (updatedThreshold.isEmpty()) {
            throw new EnvironmentThresholdNotFoundException(coffeeLotId);
        }

        return ResponseEntity.ok(EnvironmentThresholdResourceFromEntityAssembler.toResourceFromEntity(updatedThreshold.get()));
    }
}
