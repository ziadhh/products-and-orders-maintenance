package com.rohlik.productsandordersmaintenance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderId implements Serializable {

    @Column(name="orderId")
    private Integer orderId;

    @Column(name="productId")
    private Integer productId;



    /*public OrderId(){


    }

    public OrderId(Integer orderId, Integer productId) {
        this.orderId = orderId;
        this.productId = productId;
    }*/

}