package com.rohlik.productsandordersmaintenance.mapper;

import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order requestToEntity(OrderRequest orderRequest);
}
