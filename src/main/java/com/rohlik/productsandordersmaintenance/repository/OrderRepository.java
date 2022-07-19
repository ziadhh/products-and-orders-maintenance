package com.rohlik.productsandordersmaintenance.repository;

import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {


    //List<Order> saveAll(List<Order> order);

}
