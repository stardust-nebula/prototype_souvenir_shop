package com.example.souvenirstore.controller;

import com.example.souvenirstore.emailSending.EmailSenderService;
import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/byOrderNumber")
    public ResponseEntity<Object> getOrderByOrderNumber(@RequestHeader(name = "X-Token") UUID xToken, String orderNumber) {
        Optional<Order> order = orderService.getOrderByOrderNumber(orderNumber);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/userOrders")
    public ResponseEntity<Object> getOrdersByUser(@RequestHeader(name = "X-Token") UUID xToken, long userId) {
        try {
            List<Order> orderList = orderService.getUserOrderList(userId, xToken);
            return new ResponseEntity<>(orderList, HttpStatus.OK);
        }catch (ExceptionHandler e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/complete/{orderId}")
    public ResponseEntity<Object> completeOrder(@RequestHeader(name = "X-Token") UUID xToken, @PathVariable long orderId) {
        try {
            orderService.completeOrder(orderId, xToken);
            Optional<Order> order = orderService.getOrderById(orderId);
            emailSenderService.sendMail(order.get());
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (ExceptionHandler e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getOrderDetails/{orderId}")
    public ResponseEntity<Object> getOrderDetails(@RequestHeader(name = "X-Token") UUID xToken, @PathVariable long orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

}
