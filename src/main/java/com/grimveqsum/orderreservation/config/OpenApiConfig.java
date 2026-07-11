package com.grimveqsum.orderreservation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Reservation Service API",
                version = "1.0",
                description = "Backend service for creating orders and reserving products with stock control"
        )
)
public class OpenApiConfig {
}