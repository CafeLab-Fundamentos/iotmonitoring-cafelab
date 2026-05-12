package com.cafemetrix.cafelab.iotmonitoring.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {

    @Test
    void shouldCreateUserId() {
        // Act
        UserId userId = new UserId(1L);

        // Assert
        assertEquals(1L, userId.userId());
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNegative() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new UserId(-1L));
    }
}
