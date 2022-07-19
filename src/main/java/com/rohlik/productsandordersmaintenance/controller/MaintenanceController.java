package com.rohlik.productsandordersmaintenance.controller;

import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductResponse;
import com.rohlik.productsandordersmaintenance.entity.Product;
import com.rohlik.productsandordersmaintenance.mapper.ProductMapper;
import com.rohlik.productsandordersmaintenance.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/maintenance")
@Slf4j
public class MaintenanceController {

    @Autowired
    private ProductService productService;



    @Autowired
    ProductMapper productMapper;


    @PostMapping("/addProduct")
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Adding new product..");
        return    new ResponseEntity(productService.saveProduct(product),  HttpStatus.OK);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<ProductResponse> deleteProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Deleting new product..");
        return    new ResponseEntity(productService.deleteProduct(product),  HttpStatus.OK);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<ProductResponse> updateProduct(@RequestBody ProductRequest productRequest){

        Product product=productMapper.requestToEntity(productRequest);
        log.info("Updating new product..");
        return    new ResponseEntity(productService.updateProduct(product),  HttpStatus.OK);
    }


}
