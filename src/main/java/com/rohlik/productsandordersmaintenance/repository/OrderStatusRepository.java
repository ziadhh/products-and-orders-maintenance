package com.rohlik.productsandordersmaintenance.repository;

import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderStatusRepository extends JpaRepository<OrderStatus,Integer> {

    @Query("select ord from OrderStatus ord where ord.id=:id")
    OrderStatus findByOrderId(@Param("id") Integer id);

    @Query("select ord from OrderStatus ord where ord.status=:status and datediff(minute,last_update,current_timestamp)>1")
    List<OrderStatus> findUnpaidOrders(@Param("status") String status);
}
