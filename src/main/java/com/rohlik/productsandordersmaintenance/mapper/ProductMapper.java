package com.rohlik.productsandordersmaintenance.mapper;

import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product requestToEntity(ProductRequest productRequest);
}
