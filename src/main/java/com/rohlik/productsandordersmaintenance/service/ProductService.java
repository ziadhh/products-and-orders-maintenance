package com.rohlik.productsandordersmaintenance.service;


import com.rohlik.productsandordersmaintenance.entity.Product;
import com.rohlik.productsandordersmaintenance.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) {
        log.debug("Product will be saved");
        product= productRepository.save(product);
        log.debug("Product saved");
        return product;
    }

    public Product deleteProduct(Product product) {
        log.debug("Product will be deleted");
        product=productRepository.deleteById(product.getProductId());
        log.debug("Product deleted");
        return product;
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
