package com.rohlik.productsandordersmaintenance.service;


import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductDTO;
import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.OrderId;
import com.rohlik.productsandordersmaintenance.entity.Product;
import com.rohlik.productsandordersmaintenance.repository.OrderRepository;
import com.rohlik.productsandordersmaintenance.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order saveOrder(Order order) throws Exception{
        log.debug("Order will be saved");
        order= orderRepository.save(order);
        int productId=order.getId().getProductId();
        Product product=productRepository.findById(productId);
        Integer quantityInStock=product.getQuantityInStock();
        Integer updatedQuantityInStock=quantityInStock-order.getQuantity();
        product.setQuantityInStock(updatedQuantityInStock);
        if (updatedQuantityInStock<0){
            log.warn("The order can't be done, the are "+ Math.abs(updatedQuantityInStock) +" items for product with id "+ productId+" missing");
            //int a=20/0;
            throw new RuntimeException("Missing Items"); // si pongo exception la transacionalidad no va..
        }
        productRepository.save(product);
        log.debug("order saved");
        return order;
    }


    public void saveOrder(OrderRequest orderRequest) throws Exception{

        List<ProductDTO> productsInOrder = orderRequest.getProducts();
        log.info("Order will be saved");
        for (ProductDTO prod : productsInOrder) {
            Order order = new Order();
            OrderId orderId = new OrderId(orderRequest.getOrderId(), prod.getProductId());
            order.setId(orderId);
            order.setQuantity(prod.getQuantity());
            orderRepository.save(order);
            int productId = order.getId().getProductId();
            Product product = productRepository.findById(productId);
            Integer quantityInStock = product.getQuantityInStock();
            Integer updatedQuantityInStock = quantityInStock - order.getQuantity();
            if (updatedQuantityInStock<0){
                log.warn("The order can't be done, the are "+ Math.abs(updatedQuantityInStock) +" items for product with id "+ productId+" missing");
                throw new RuntimeException("Missing Items");
            }
            product.setQuantityInStock(updatedQuantityInStock);
            productRepository.save(product);
        }
        log.info("order saved");

    }


}
