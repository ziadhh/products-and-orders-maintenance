package com.rohlik.productsandordersmaintenance;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductsAndOrdersMaintenanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductsAndOrdersMaintenanceApplication.class, args);
	}


}
