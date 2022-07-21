package com.rohlik.productsandordersmaintenance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {

    private int orderId;
    private List<ProductDTO> products;
    private List<ProductDTO> missingProducts;
    private String status;

}
