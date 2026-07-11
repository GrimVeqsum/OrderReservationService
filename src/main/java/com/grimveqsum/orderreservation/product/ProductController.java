package com.grimveqsum.orderreservation.product;

import com.grimveqsum.orderreservation.product.dto.CreateProductRequest;
import com.grimveqsum.orderreservation.product.dto.ProductResponse;
import com.grimveqsum.orderreservation.product.dto.UpdateProductQuantityRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = productService.create(request);

        return ResponseEntity
                .created(URI.create("/products/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        List<ProductResponse> response = productService.findAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse response = productService.findById(id);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ProductResponse> updateQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductQuantityRequest request
    ) {
        ProductResponse response = productService.updateQuantity(id, request);

        return ResponseEntity.ok(response);
    }
}