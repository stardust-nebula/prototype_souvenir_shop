package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.Product;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public void save(Product product) throws ExceptionHandler {
        boolean isProductNameExist = existsProductByName(product.getName());
        if (isProductNameExist) {
            throw new ExceptionHandler("Product with \"" + product.getName() + "\" name already exist");
        } else {
            product.setCreationDate(LocalDateTime.now());
            product.setUpdateDate(LocalDateTime.now());
            productRepository.save(product);
        }
    }

    public void changeProductAvailabilityStatus(long productId, boolean status) {
        boolean isEnabled;
        if (status) {
            isEnabled = true;
        } else {
            isEnabled = false;
        }
        productRepository.changeProductAvailabilityStatus(productId, isEnabled);
    }

    public void changeProductName(long productId, String productName) throws ExceptionHandler {
        boolean isProductNameExist = existsProductByName(productName);
        if (isProductNameExist) {
            throw new ExceptionHandler("Product with \"" + productName + "\" name already exist");
        } else {
            productRepository.changeProductName(productId, productName);
        }
    }

    public void changeProductDescription(long productId, String productDescription) {
        productRepository.changeProductDescription(productId, productDescription);
    }

    public void changeProductPrice(long productId, double productPrice) {
        productRepository.changeProductPrice(productId, productPrice);
    }

    public List<Product> getProductsByName(String productName) {
        return productRepository.getProductsByNameIsContaining(productName);
    }

    public List<Product> getProductsByPrice(double price) {
        return productRepository.getProductsByPrice(price);
    }

    public List<Product> getProductsByStatus(boolean status) {
        return productRepository.getProductsByStatusIsEnabled(status);
    }

    private boolean existsProductByName(String name) {
        return productRepository.existsProductByName(name);
    }

    public Optional<Product> getProductById(long productId){
        return productRepository.getProductById(productId);
    }
}
