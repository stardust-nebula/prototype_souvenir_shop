package com.example.souvenirstore.controller;

import com.example.souvenirstore.entity.Product;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.service.ProductService;
import com.example.souvenirstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getProductsByName")
    public List<Product> getProductsByName(@RequestHeader(name = "X-Token") UUID xToken, String productName) {
        return productService.getProductsByName(productName);
    }

    @GetMapping("/getProductsByPrice")
    public List<Product> getProductsByPrice(@RequestHeader(name = "X-Token") UUID xToken, double price) {
        return productService.getProductsByPrice(price);
    }

    @GetMapping("/getProducts")
    public List<Product> getProductsByStatus(@RequestHeader(name = "X-Token") UUID xToken) {
        return productService.getProductsByStatus(true);
    }

}
