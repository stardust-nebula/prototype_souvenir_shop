package com.example.souvenirstore.controller;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.entity.Product;
import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.entity.UserRoleEnum;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.service.OrderService;
import com.example.souvenirstore.service.ProductService;
import com.example.souvenirstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/changeUserRole")
    public ResponseEntity<Object> changeUserRole(@RequestHeader(name = "X-Token") UUID xToken, long userId, UserRoleEnum userRoleEnum){
        userService.changeUserRole(userId, userRoleEnum.name());
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public List<User> getUsers(@RequestHeader(name = "X-Token") UUID xToken) {
        return userService.findAll();
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Object> addProduct(@RequestHeader(name = "X-Token") UUID xToken, @RequestBody Product product) {
        try {
            productService.save(product);
            return new ResponseEntity<>(product, HttpStatus.CREATED);
        } catch (com.example.souvenirstore.exception.ExceptionHandler e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/changeProductStatus")
    public void changeProductStatus(@RequestHeader(name = "X-Token") UUID xToken, long productId, boolean status){
        productService.changeProductAvailabilityStatus(productId, status);
    }

    @PostMapping("/changeProductName")
    public ResponseEntity changeProductName(@RequestHeader(name = "X-Token") UUID xToken, long productId,String productName){
        try {
            productService.changeProductName(productId, productName);
            return ResponseEntity.ok().body("Product name is changed");
        } catch (ExceptionHandler e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/changeProductDescription")
    public void changeProductDescription(@RequestHeader(name = "X-Token") UUID xToken, long productId, String productDescription) {
        productService.changeProductDescription(productId, productDescription);
    }

    @PostMapping("/changeProductPrice")
    public void changeProductPrice(@RequestHeader(name = "X-Token") UUID xToken, long productId, double productPrice) {
        productService.changeProductPrice(productId, productPrice);
    }

    @GetMapping("/getDisabledProducts")
    public List<Product> getProductsByStatus(@RequestHeader(name = "X-Token") UUID xToken) {
        return productService.getProductsByStatus(false);
    }

    @PostMapping("cancel/{orderId}")
    public ResponseEntity<Object> cancelOrder(@RequestHeader(name = "X-Token") UUID xToken, @PathVariable long orderId) {
        try {
            orderService.cancelOrder(orderId);
            Optional<Order> order = orderService.getOrderById(orderId);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (ExceptionHandler e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
