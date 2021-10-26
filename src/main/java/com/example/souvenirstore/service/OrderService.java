package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.entity.OrderItems;
import com.example.souvenirstore.entity.OrderStatus;
import com.example.souvenirstore.entity.User;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.repository.OrderItemsRepository;
import com.example.souvenirstore.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
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
            throw new ExceptionHandler("No order");
        }
        if (!isOrderInTheExactStatus(orderId, OrderStatus.INITIAL)) {
            throw new ExceptionHandler("Order is not in the \"INITIAL\" status to complete");
        }
        if (isOrderEmpty(orderId)) {
            throw new ExceptionHandler("Order is empty");
        }
        Optional<Order> order = getOrderById(orderId);

        User orderUser = order.get().getUser();
        User tokenUser = tokenService.getTokenByUuid(xToken).getUser();

        if (!(orderUser.getId() == tokenUser.getId())){
            throw new ExceptionHandler("Order is not available for you");
        }

        order.get().setStatus(OrderStatus.COMPLETE);
        order.get().setCompletionDate(LocalDateTime.now());
        orderRepository.save(order.get());
    }

    public void cancelOrder(long orderId) throws ExceptionHandler {
        if (!isOrderExistByOrderId(orderId)) {
            throw new ExceptionHandler("No order");
        }
        if (!isOrderInTheExactStatus(orderId, OrderStatus.COMPLETE)) {
            throw new ExceptionHandler("Order is not in the \"COMPLETE\" status to cancel");
        }
        Optional<Order> order = getOrderById(orderId);
        order.get().setStatus(OrderStatus.CANCELED);
        orderRepository.save(order.get());
    }

    private String generateNewOrderNumber() {
        String orderPrefix = "X-";
        if (!(orderRepository.countOrders() > 0)) {
            return orderPrefix + "1";
        }
        String theLastOrderNumber = orderRepository.getTheLastOrderNumber().get();
        String parsedOrderNumber = theLastOrderNumber.substring(orderPrefix.length());
        long number = Long.parseLong(parsedOrderNumber) + 1;
        return orderPrefix + number;
    }

    public boolean isUserHasInitialOrder(long userId) {
        Optional<Order> order = Optional.ofNullable(orderRepository.getOrderByUserIdAndInitialStatus(userId));
        if (order.isEmpty()) {
            return false;
        }
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
            throw new ExceptionHandler("Not available");
        }
        return orderRepository.getOrdersByUserId(userId);
    }

    private boolean isOrderExistByOrderId(long orderId) {
        Optional<Order> order = getOrderById(orderId);
        if (order.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isOrderInTheExactStatus(long orderId, OrderStatus orderStatus) {
        Optional<Order> order = getOrderById(orderId);
        if (order.get().getStatus().equals(orderStatus)) {
            return true;
        }
        return false;
    }

    private boolean isOrderEmpty(long orderId) {
        List<OrderItems> orderItemsList = orderItemsRepository.getListItemsByOrderId(orderId);
        long itemsOrderCount = orderItemsList.stream().count();
        if (itemsOrderCount > 0) {
            return false;
        }
        return true;
    }

}
