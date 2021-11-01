package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.entity.OrderItems;
import com.example.souvenirstore.entity.OrderStatus;
import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.repository.OrderItemsRepository;
import com.example.souvenirstore.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    public void createOrder(long userId) {
        Order order = new Order();
        order.setUser(userService.getUserById(userId));
        order.setOrderNumber(generateNewOrderNumber());
        order.setStatus(OrderStatus.INITIAL);
        order.setCreationDate(LocalDateTime.now());
        order.setTotal(0);
        orderRepository.save(order);
    }

    public Optional<Order> getOrderByOrderNumber(String orderNumber) {
        return orderRepository.getOrderByOrderNumber(orderNumber);
    }

    public void completeOrder(long orderId, UUID xToken) throws ExceptionHandler {
        if (!isOrderExistByOrderId(orderId)) {
            log.warn("IN completeOrder: Order with {} id is not exist", orderId);
            throw new ExceptionHandler("No order");
        }
        if (!isOrderInTheExactStatus(orderId, OrderStatus.INITIAL)) {
            log.warn("IN completeOrder: Order with {} id is not in INITIAL status", orderId);
            throw new ExceptionHandler("Order is not in the \"INITIAL\" status to complete");
        }
        if (isOrderEmpty(orderId)) {
            log.warn("IN completeOrder: Order with {} id is empty", orderId);
            throw new ExceptionHandler("Order is empty");
        }
        Optional<Order> order = getOrderById(orderId);

        User orderUser = order.get().getUser();
        User tokenUser = tokenService.getTokenByUuid(xToken).getUser();

        if (!(orderUser.getId() == tokenUser.getId())){
            log.warn("IN completeOrder: User does not have access to complete order", orderId);
            throw new ExceptionHandler("Order is not available for you");
        }

        order.get().setStatus(OrderStatus.COMPLETE);
        order.get().setCompletionDate(LocalDateTime.now());
        orderRepository.save(order.get());
        log.info("IN completeOrder: Order with {} is COMPLETED", orderId);
    }

    public void cancelOrder(long orderId) throws ExceptionHandler {
        if (!isOrderExistByOrderId(orderId)) {
            log.warn("IN cancelOrder: Order with {} id is not exist", orderId);
            throw new ExceptionHandler("No order");
        }
        if (!isOrderInTheExactStatus(orderId, OrderStatus.COMPLETE)) {
            log.warn("IN cancelOrder: Order with {} id is not in COMPLETE status", orderId);
            throw new ExceptionHandler("Order is not in the \"COMPLETE\" status to cancel");
        }
        Optional<Order> order = getOrderById(orderId);
        order.get().setStatus(OrderStatus.CANCELED);
        orderRepository.save(order.get());
        log.info("IN cancelOrder: Order with {} id is CANCELED", orderId);
    }

    private String generateNewOrderNumber() {
        String orderPrefix = "X-";
        String orderNumber;
        if (!(orderRepository.countOrders() > 0)) {
            return orderPrefix + "1";
        }else {
            String theLastOrderNumber = orderRepository.getTheLastOrderNumber().get();
            String parsedOrderNumber = theLastOrderNumber.substring(orderPrefix.length());
            long number = Long.parseLong(parsedOrderNumber) + 1;
            orderNumber = orderPrefix + number;
        }
        log.info("IN generateNewOrderNumber: New order number is created - {}", orderNumber);
        return orderNumber;
    }

    public boolean isUserHasInitialOrder(long userId) {
        Optional<Order> order = Optional.ofNullable(orderRepository.getOrderByUserIdAndInitialStatus(userId));
        if (order.isEmpty()) {
            log.info("IN isUserHasInitialOrder: User with {} id does not have order in the INITIAL status", userId);
            return false;
        }
        log.info("IN isUserHasInitialOrder: User with {} id has order in the INITIAL status", userId);
        return true;
    }

    public Optional<Order> getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId);
    }

    public Order getOrderByUserIdAndStatusInitial(long userId) {
        return orderRepository.getOrderByUserIdAndInitialStatus(userId);
    }

    public void updateOrderTotal(long orderId) {
        List<OrderItems> orderItemsList = orderItemsRepository.getListItemsByOrderId(orderId);
        double orderSum = orderItemsList.stream()
                .mapToDouble(e -> e.getProduct().getPrice())
                .sum();
        orderRepository.updateOrderTotal(orderId, orderSum);
    }

    public double getOrderTotal(long orderId) {
        return orderRepository.getOrderTotal(orderId);
    }

    public void deleteOrder(long orderId) {
        orderRepository.deleteOrderById(orderId);
    }

    public List<Order> getUserOrderList(long userId, UUID xToken) throws ExceptionHandler {
        User tokenUser = tokenService.getTokenByUuid(xToken).getUser();

        boolean isUserTokenEqualsUserId = tokenUser.getId() == userId;
        boolean isUserTokenRoleEqualsAdmin = tokenUser.getUserRole().name().equals("ADMIN");

        if (!(isUserTokenEqualsUserId || isUserTokenRoleEqualsAdmin)){
            log.warn("IN getUserOrderList: User cannot get list of order", userId);
            throw new ExceptionHandler("Not available");
        }
        return orderRepository.getOrdersByUserId(userId);
    }

    private boolean isOrderExistByOrderId(long orderId) {
        Optional<Order> order = getOrderById(orderId);
        if (order.isEmpty()) {
            log.info("IN isOrderExistByOrderId: No order by order {} id", orderId);
            return false;
        }
        log.info("IN isOrderExistByOrderId: Order by order {} id exists", orderId);
        return true;
    }

    public boolean isOrderInTheExactStatus(long orderId, OrderStatus orderStatus) {
        Optional<Order> order = getOrderById(orderId);
        if (order.get().getStatus().equals(orderStatus)) {
            log.info("IN isOrderInTheExactStatus: Order by order {} id is in the requested status", orderId);
            return true;
        }
        log.info("IN isOrderInTheExactStatus: Order by order {} id is not in the requested status", orderId);
        return false;
    }

    private boolean isOrderEmpty(long orderId) {
        List<OrderItems> orderItemsList = orderItemsRepository.getListItemsByOrderId(orderId);
        long itemsOrderCount = orderItemsList.stream().count();
        if (itemsOrderCount > 0) {
            log.info("IN isOrderEmpty: Order by order {} id is not empty", orderId);
            return false;
        }
        log.info("IN isOrderEmpty: Order by order {} id is empty", orderId);
        return true;
    }
}
