package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.entity.OrderItems;
import com.example.souvenirstore.entity.OrderStatus;
import com.example.souvenirstore.entity.Product;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.repository.OrderItemsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class OrderItemsService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemsRepository orderItemsRepository;


    public void saveItem(long productId, long userId) throws ExceptionHandler {
        if (!isProductEnabled(productId)) {
            log.warn("IN saveItem: product by {} id is not available", productId);
            new ExceptionHandler("Product is not available");
        }
        boolean isExist = orderService.isUserHasInitialOrder(userId);
        if (!isExist) {
            log.info("IN saveItem: No order. Create new order");
            orderService.createOrder(userId);
        }
        OrderItems orderItems = new OrderItems();
        orderItems.setProduct(productService.getProductById(productId).get());
        orderItems.setCreationDate(LocalDateTime.now());
        orderItems.setUpdateDate(LocalDateTime.now());
        Order order = orderService.getOrderByUserIdAndStatusInitial(userId);
        orderItems.setOrder(order);
        orderItemsRepository.save(orderItems);
        orderService.updateOrderTotal(order.getId());
        log.info("IN saveItem: product with {} id is added}", productId);
    }

    private boolean isProductEnabled(long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.get().isStatusIsEnabled()) {
            log.info("IN isProductEnabled: product with {} id is available}", productId);
            return true;
        } else {
            log.info("IN isProductEnabled: product with {} id is not available}", productId);
            return false;
        }
    }

    @Transactional
    public void deleteItem(long itemId) throws ExceptionHandler {
        if (!isOrderItemExist(itemId)) {
            log.warn("IN deleteItem: no orderItem with {} id}", itemId);
            throw new ExceptionHandler("No order item");
        }
        long orderId = orderItemsRepository.getOrderIdByOrderItemId(itemId);
        if (!orderService.isOrderInTheExactStatus(orderId, OrderStatus.INITIAL)){
            log.warn("IN deleteItem: orderItem with {} id is from the order that has not INITIAL status}", itemId);
            throw new ExceptionHandler("Cannot delete order item from inactive order");
        }
        orderItemsRepository.deleteById(itemId);
        log.info("IN deleteItem: orderItem with {} id is removed}", itemId);
        long countOrderItems = orderItemsRepository.getListItemsByOrderId(orderId)
                .stream()
                .count();
        if (countOrderItems > 0) {
            log.info("IN deleteItem: update order total}", itemId);
            orderService.updateOrderTotal(orderId);
            return;
        } else {
            log.info("IN deleteItem: No more orderItems left in the order. Deleting order with {} id}", orderId);
            orderService.deleteOrder(orderId);
            return;
        }
    }

    public long getOrderIdByOrderItemId(long orderItemId) {
        Optional<OrderItems> orderItems = orderItemsRepository.getOrderItemsById(orderItemId);
        if (orderItems.isEmpty()) {
            log.warn("IN getOrderIdByOrderItemId: No orderItem with {} id}", orderItemId);
            return 0;
        } else {
            return orderItemsRepository.getOrderIdByOrderItemId(orderItemId);
        }
    }

    public List<OrderItems> getOrderItemsByOrderId(long orderId) {
        return orderItemsRepository.getListItemsByOrderId(orderId);
    }

    private boolean isOrderItemExist(long itemId) {
        Optional<OrderItems> orderItems = orderItemsRepository.getOrderItemsById(itemId);
        if (orderItems.isEmpty()) {
            log.warn("IN isOrderItemExist: No orderItem with {} id}", itemId);
            return false;
        }
        return true;
    }

}
