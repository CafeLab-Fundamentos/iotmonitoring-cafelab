package com.cafemetrix.cafelab.iotmonitoring.domain.model.valueobjects;

import jakarta.persistence.Embeddable;

@Embeddable
public record UserId(Long userId) {
    public UserId {
        if (userId == null || userId < 0) {
            throw new IllegalArgumentException("UserId cannot be null or negative");
        }
    }
}
