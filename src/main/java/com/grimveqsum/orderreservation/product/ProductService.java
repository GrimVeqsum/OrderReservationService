package com.grimveqsum.orderreservation.product;

import com.grimveqsum.orderreservation.common.NotFoundException;
import com.grimveqsum.orderreservation.product.dto.CreateProductRequest;
import com.grimveqsum.orderreservation.product.dto.ProductResponse;
import com.grimveqsum.orderreservation.product.dto.UpdateProductQuantityRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setAvailableQuantity(request.availableQuantity());

        Product savedProduct = productRepository.save(product);

        return ProductResponse.from(savedProduct);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = findProductById(id);

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateQuantity(Long id, UpdateProductQuantityRequest request) {
        Product product = findProductById(id);

        product.setAvailableQuantity(request.availableQuantity());

        return ProductResponse.from(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
    }
}