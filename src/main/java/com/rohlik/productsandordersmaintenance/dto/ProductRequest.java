package com.rohlik.productsandordersmaintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private int productId;
    private String name;
    private int    quantityInStock;
    private double  pricePerUnit;;



}

