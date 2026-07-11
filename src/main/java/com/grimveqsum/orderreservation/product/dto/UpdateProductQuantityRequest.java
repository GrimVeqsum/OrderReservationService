package com.grimveqsum.orderreservation.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProductQuantityRequest(
        @NotNull
        @Min(0)
        Integer availableQuantity
) {
}