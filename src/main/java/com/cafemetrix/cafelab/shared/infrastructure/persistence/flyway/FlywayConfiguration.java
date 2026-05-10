package com.cafemetrix.cafelab.shared.infrastructure.persistence.flyway;

import org.springframework.context.annotation.Configuration;

/**
 * Marcador vacío: antes se llamaba a {@code Flyway.migrate()} aquí (ignoraba {@code spring.flyway.enabled}).
 * Las migraciones las gestiona solo el auto-config de Spring Boot cuando {@code spring.flyway.enabled=true}.
 */
@Configuration
public class FlywayConfiguration {
}
