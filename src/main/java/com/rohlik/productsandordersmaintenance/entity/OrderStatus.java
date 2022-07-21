package com.rohlik.productsandordersmaintenance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


@Entity
@Table(name= "ORDER_STATUS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStatus {

    @Id
    private Integer orderId;
    private String status;
    private LocalDateTime lastUpdate;


}
