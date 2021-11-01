package com.example.souvenirstore.controller;

import com.example.souvenirstore.entity.Product;
import com.example.souvenirstore.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/getProductsByName")
    @ApiOperation(value = "Get products by product name")
    public List<Product> getProductsByName(@RequestHeader(name = "X-Token") UUID xToken, String productName) {
        return productService.getProductsByName(productName);
    }

    @GetMapping("/getProductsByPrice")
    @ApiOperation(value = "Get products by product price")
    public List<Product> getProductsByPrice(@RequestHeader(name = "X-Token") UUID xToken, double price) {
        return productService.getProductsByPrice(price);
    }

    @GetMapping("/getProducts")
    @ApiOperation(value = "Get all available products")
    public List<Product> getProductsByStatus(@RequestHeader(name = "X-Token") UUID xToken) {
        return productService.getProductsByStatus(true);
    }
}
