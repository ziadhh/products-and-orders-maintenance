package com.rohlik.productsandordersmaintenance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "ORDERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private OrderId id;
    private int quantity;

}
