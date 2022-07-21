package com.rohlik.productsandordersmaintenance.service;


import com.rohlik.productsandordersmaintenance.constants.ProductConstants;
import com.rohlik.productsandordersmaintenance.entity.Product;
import com.rohlik.productsandordersmaintenance.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product saveProduct(Product product) {
        log.debug("Product will be saved");
        product= productRepository.save(product);
        log.debug("Product saved");
        return product;
    }

    public String deleteProduct(Product product) throws EmptyResultDataAccessException {
        log.debug("Product will be deleted");
        String message;
        try {
            productRepository.deleteById(product.getProductId());
            message= ProductConstants.msgProductDeleted;
        }catch(Exception ex){
            message=ProductConstants.msgProductNotDeleted;
            log.error("Product has not been deleted");
        }
        log.debug("Product deleted");
        return message;
    }

    @Transactional
   public Product updateProduct(Product product) {
        log.debug("Product will be updated");
        productRepository.updateById(product.getProductId(),product.getName(),product.getQuantityInStock(),product.getPricePerUnit());
        log.debug("Product updated");
        product=productRepository.findById(product.getProductId());
        return product;
    }



}
