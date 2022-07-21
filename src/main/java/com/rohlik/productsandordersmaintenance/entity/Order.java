package com.rohlik.productsandordersmaintenance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name= "ORDERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    private OrderId id;
    private int quantity;


}
