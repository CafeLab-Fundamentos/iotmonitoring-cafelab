package com.cafemetrix.cafelab.production.infrastructure.clients;

import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeLotSummary;
import com.cafemetrix.cafelab.production.interfaces.acl.CoffeeproductionContextFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.Optional;

@Service
public class CoffeeproductionMonolithClient implements CoffeeproductionContextFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeproductionMonolithClient.class);
    private static final String USER_ID_HEADER = "X-User-Id";

    private final RestClient restClient;

    public CoffeeproductionMonolithClient(@Value("${management.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Optional<CoffeeLotSummary> getCoffeeLotById(Long coffeeLotId) {
        if (coffeeLotId == null) {
            return Optional.empty();
        }
        try {
            Map<String, Object> body = restClient.get()
                    .uri("/api/v1/coffee-lots/{id}", coffeeLotId)
                    .headers(headers -> currentUserIdHeader().ifPresent(v -> headers.set(USER_ID_HEADER, v)))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (body == null) {
                return Optional.empty();
            }

            Long id = readLong(body, "id", "coffee_lot_id", "coffeeLotId");
            Long userId = readLong(body, "userId", "user_id");

            if (id == null) {
                LOGGER.warn("Respuesta de Management para lote {} no trae un id reconocible. Body: {}", coffeeLotId, body.keySet());
                return Optional.empty();
            }

            return Optional.of(new CoffeeLotSummary(id, userId));
        } catch (Exception ex) {
            LOGGER.warn("Fallo al consultar lote {} en Management: {}", coffeeLotId, ex.getMessage());
            return Optional.empty();
        }
    }

    private static Long readLong(Map<String, Object> body, String... aliases) {
        for (String alias : aliases) {
            Object value = body.get(alias);
            if (value == null) {
                continue;
            }
            try {
                return Long.valueOf(value.toString().trim());
            } catch (NumberFormatException ex) {
                LOGGER.warn("Campo '{}' no es un Long valido: {}", alias, value);
            }
        }
        return null;
    }

    private static Optional<String> currentUserIdHeader() {
        var attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes servletAttributes)) {
            return Optional.empty();
        }
        HttpServletRequest request = servletAttributes.getRequest();
        String header = request.getHeader(USER_ID_HEADER);
        return (header == null || header.isBlank()) ? Optional.empty() : Optional.of(header);
    }
}
