package com.rohlik.productsandordersmaintenance.controller;

import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductDTO;
import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.OrderId;
import com.rohlik.productsandordersmaintenance.entity.Product;
import com.rohlik.productsandordersmaintenance.mapper.OrderMapper;
import com.rohlik.productsandordersmaintenance.mapper.ProductMapper;
import com.rohlik.productsandordersmaintenance.service.OrderService;
import com.rohlik.productsandordersmaintenance.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/maintenance")
@Slf4j
public class MaintenanceController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;



    @Autowired
    ProductMapper productMapper;

    @Autowired
    OrderMapper orderMapper;


    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Adding new product..");
        return    new ResponseEntity(productService.saveProduct(product),  HttpStatus.OK);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Product> deleteProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Deleting new product..");
        return    new ResponseEntity(productService.deleteProduct(product),  HttpStatus.OK);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Updating new product..");
        return    new ResponseEntity(productService.updateProduct(product),  HttpStatus.OK);
    }



    @PostMapping("/addOrder")
    @Transactional
    public /*ResponseEntity<Order>*/ void addOrder(@RequestBody OrderRequest orderRequest) throws Exception{

        List<Order> orders=new ArrayList<>();
        List<ProductDTO> productsInOrder=orderRequest.getProducts();
        /*for(ProductDTO prod:productsInOrder) {
            Order order=new Order();
            OrderId orderId=new OrderId(orderRequest.getOrderId(),prod.getProductId());
            order.setId(orderId);
            order.setQuantity(prod.getQuantity());
            orders.add(order);
            orderService.saveOrder(order);
        }*/
        log.info("Adding new order..");
        orderService.saveOrder(orderRequest);


        //return    new ResponseEntity(orderService.saveOrder(orders),  HttpStatus.OK);
    }


}
