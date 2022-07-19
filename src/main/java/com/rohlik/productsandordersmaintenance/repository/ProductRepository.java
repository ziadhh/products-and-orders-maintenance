package com.rohlik.productsandordersmaintenance.repository;

import com.rohlik.productsandordersmaintenance.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<Product,Integer> {


    Product save(Product product);

    Product deleteById(@Param("productId") int productId);

    Product findById(@Param("productId") int productId);

    @Modifying
    @Query("update Product prod set prod.name= :name , prod.quantityInStock= :quantityInStock, prod.pricePerUnit= :pricePerUnit where prod.productId= :productId  ")
    void updateById(@Param("productId") Integer productId,@Param("name") String name,@Param("quantityInStock") Integer quantityInStock,@Param("pricePerUnit") Double pricePerUnit);




}
