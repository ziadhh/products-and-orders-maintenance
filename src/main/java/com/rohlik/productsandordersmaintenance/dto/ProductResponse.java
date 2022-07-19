package com.rohlik.productsandordersmaintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private int brandId;
    private String productId;
    private int priceList;
    private Date startDate;
    private Date endDate;
    private double price;


}
