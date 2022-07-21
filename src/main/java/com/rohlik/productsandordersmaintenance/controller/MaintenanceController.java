package com.rohlik.productsandordersmaintenance.controller;

import com.rohlik.productsandordersmaintenance.dto.OrderDTO;
import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductDTO;
import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.OrderId;
import com.rohlik.productsandordersmaintenance.entity.OrderStatus;
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

    @GetMapping("/getProducts")
    public ResponseEntity<List<Product>> getProducts(){

        log.info("Getting all products..");
        return    new ResponseEntity(productService.getProducts(),  HttpStatus.OK);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<Product> addProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Adding new product..");
        return    new ResponseEntity(productService.saveProduct(product),  HttpStatus.OK);
    }

    @PostMapping("/deleteProduct")
    public ResponseEntity<String> deleteProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Deleting new product..");
        return    new ResponseEntity(productService.deleteProduct(product),  HttpStatus.OK);
    }

    @PostMapping("/updateProduct")
    public ResponseEntity<Product> updateProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Updating new product..");
        return    new ResponseEntity(productService.updateProduct(product),  HttpStatus.OK);
    }


    @PostMapping("/addOrder")
    public ResponseEntity<OrderDTO> addOrder(@RequestBody OrderRequest orderRequest){
        List<Order> orders=new ArrayList<>();
        List<ProductDTO> productsInOrder=orderRequest.getProducts();
        log.info("Adding new order..");
        return    new ResponseEntity(orderService.saveOrder(orderRequest),  HttpStatus.OK);
    }

    @PostMapping("/payOrder/{id}")
    public ResponseEntity<OrderStatus> payOrder(@PathVariable("id") Integer id){
        log.info("Paying  order..");
        return    new ResponseEntity(orderService.payOrder(id),  HttpStatus.OK);
    }

    @PostMapping("/cancelOrder/{id}")
    public ResponseEntity<OrderStatus> cancelOrder(@PathVariable("id") Integer id){
        log.info("cancelling order..");
        return    new ResponseEntity(orderService.cancelOrder(id,true),  HttpStatus.OK);
    }


}
