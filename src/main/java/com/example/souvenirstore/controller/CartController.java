package com.example.souvenirstore.controller;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.service.OrderItemsService;
import com.example.souvenirstore.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderItemsService orderItemsService;

    @Autowired
    private OrderService orderService;


    @PostMapping("/addItem")
    @ApiOperation(value = "Add product item to Cart")
    public ResponseEntity<Object> addItemToCart(@RequestHeader(name = "X-Token") UUID xToken, long productId, long userId) {
        try {
            orderItemsService.saveItem(productId, userId);

            Order order = orderService.getOrderByUserIdAndStatusInitial(userId);
            order.setTotal(orderService.getOrderTotal(order.getId()));
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (ExceptionHandler e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/deleteItem")
    @ApiOperation(value = "Delete product item from Cart")
    public ResponseEntity<Object> deleteItemInCart(@RequestHeader(name = "X-Token") UUID xToken, long orderItemId) {
        try {
            long orderId = orderItemsService.getOrderIdByOrderItemId(orderItemId);
            orderItemsService.deleteItem(orderItemId);
            Optional<Order> order = orderService.getOrderById(orderId);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (ExceptionHandler e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
