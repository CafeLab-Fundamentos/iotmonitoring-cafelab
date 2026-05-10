package com.cafemetrix.cafelab.iotmonitoring.infrastructure.authorization.sfs.support;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CurrentProfileIdResolver {

    private final HttpServletRequest request;

    public CurrentProfileIdResolver(HttpServletRequest request) {
        this.request = request;
    }

    public Optional<Long> resolveProfileId() {
        String userIdHeader = request.getHeader("X-User-Id");
        if (userIdHeader != null && !userIdHeader.isBlank()) {
            try {
                return Optional.of(Long.parseLong(userIdHeader));
            } catch (NumberFormatException e) {
                // Return empty if header is not a valid number
                return Optional.empty();
            }
        }
        
        // Fallback to 1L for easy testing directly through Swagger
        // En producción el API Gateway siempre inyectaría este header.
        return Optional.of(1L);
    }
}
