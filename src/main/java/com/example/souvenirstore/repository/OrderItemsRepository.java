package com.example.souvenirstore.repository;

import com.example.souvenirstore.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {

    @Query(value = "select * from order_items", nativeQuery = true)
    List<OrderItems> getOrderItems();

    @Query(value = "select * from order_items where order_id =?1", nativeQuery = true)
    List<OrderItems> getListItemsByOrderId(long orderId);

    @Query(value = "select order_id from order_items where id =?1 ", nativeQuery = true)
    long getOrderIdByOrderItemId(long orderItemId);

    Optional<OrderItems> getOrderItemsById(long orderItemId);




}
