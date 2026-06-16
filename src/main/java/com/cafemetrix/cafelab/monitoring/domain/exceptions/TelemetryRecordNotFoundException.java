package com.cafemetrix.cafelab.monitoring.domain.exceptions;

public class TelemetryRecordNotFoundException extends RuntimeException {
    public TelemetryRecordNotFoundException(Long coffeeLotId) {
        super("No se encontro historial de telemetria para el lote con ID: " + coffeeLotId);
    }
}
