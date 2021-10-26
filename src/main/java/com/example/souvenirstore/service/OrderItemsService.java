package com.example.souvenirstore.service;

import com.example.souvenirstore.entity.Order;
import com.example.souvenirstore.entity.OrderItems;
import com.example.souvenirstore.entity.OrderStatus;
import com.example.souvenirstore.entity.Product;
import com.example.souvenirstore.exception.ExceptionHandler;
import com.example.souvenirstore.repository.OrderItemsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OrderItemsService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemsRepository orderItemsRepository;


    public void saveItem(long productId, long userId) throws ExceptionHandler {
        if (!isProductEnabled(productId)) {
            new ExceptionHandler("Product is not available"); // Todo add Exception
        }
        boolean isExist = orderService.isUserHasInitialOrder(userId);
        if (!isExist) {
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
    }

    private boolean isProductEnabled(long productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.get().isStatusIsEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    // Todo
    @Transactional
    public void deleteItem(long itemId) throws ExceptionHandler {
        Optional<OrderItems> orderItems = orderItemsRepository.getOrderItemsById(itemId);
        if (!isOrderItemExist(itemId)) {
            throw new ExceptionHandler("No order item");
        }

        long orderId = orderItemsRepository.getOrderIdByOrderItemId(itemId);

        if (!orderService.isOrderInTheExactStatus(orderId, OrderStatus.INITIAL)){
            throw new ExceptionHandler("Cannot delete order item from inactive order");
        }

        orderItemsRepository.deleteById(itemId);
        long countOrderItems = orderItemsRepository.getListItemsByOrderId(orderId)
                .stream()
                .count();

        if (countOrderItems > 0) {
            orderService.updateOrderTotal(orderId);
            return;
        } else {
            orderService.deleteOrder(orderId);
            return;
        }
    }

    public long getOrderIdByOrderItemId(long orderItemId) {
        Optional<OrderItems> orderItems = orderItemsRepository.getOrderItemsById(orderItemId);
        if (orderItems.isEmpty()) {
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
            return false;
        }
        return true;
    }

}
