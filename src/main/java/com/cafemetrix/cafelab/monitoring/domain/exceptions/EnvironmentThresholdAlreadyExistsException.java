package com.cafemetrix.cafelab.monitoring.domain.exceptions;

public class EnvironmentThresholdAlreadyExistsException extends RuntimeException {
    public EnvironmentThresholdAlreadyExistsException(Long coffeeLotId) {
        super("Ya existe una configuracion de umbrales ambientales registrada para el lote con ID: " + coffeeLotId);
    }
}
