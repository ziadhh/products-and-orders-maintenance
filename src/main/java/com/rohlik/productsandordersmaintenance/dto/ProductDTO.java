package com.rohlik.productsandordersmaintenance.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private int productId;
    private int quantity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int missedQuantity;

}
