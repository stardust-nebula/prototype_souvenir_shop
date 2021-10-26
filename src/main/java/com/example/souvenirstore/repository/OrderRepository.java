package com.example.souvenirstore.repository;

import com.example.souvenirstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> getOrderByOrderNumber(String orderNumber);

    @Modifying
    @Query("update Order o set o.status =?2 where o.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void changeOrderStatus(long orderId, String orderStatus);

    @Modifying
    @Query("update Order o set o.completionDate =?2 where o.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void setCompletionDate(long orderId, LocalDate localDate);

    @Query(value = "select order_number from orders order by order_number DESC limit 1", nativeQuery = true)
    @Transactional(readOnly = true)
    Optional<String> getTheLastOrderNumber();

    @Query(value = "select count(*) from orders", nativeQuery = true)
    long countOrders();

    @Query(value = "select * from orders where id =?1", nativeQuery = true)
    Optional<Order> getOrderById(long orderId);

    @Query(value = "select * from orders where user_id =?1 and status = 'INITIAL'", nativeQuery = true)
    Order getOrderByUserIdAndInitialStatus(long userId);

    @Modifying
    @Query("update Order o set o.total =?2 where o.id =?1")
    @Transactional(rollbackFor = Exception.class)
    void updateOrderTotal(long orderId, double orderTotal);

    @Query(value = "select total from orders where id =?1", nativeQuery = true)
    double getOrderTotal(long orderId);

    @Modifying
    @Query(value = "delete from orders where id =?1", nativeQuery = true)
    void deleteOrderById(long orderId);

    List<Order> getOrdersByUserId(long userId);


}
