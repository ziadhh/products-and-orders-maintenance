package com.rohlik.productsandordersmaintenance;

import com.rohlik.productsandordersmaintenance.constants.ProductConstants;
import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.entity.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductsAndOrdersMaintenanceApplicationTests {

	private String uri;
	private RestTemplate restTemplate;

	@Value("${server.port}")
	private int port;


	Product responseExpected;
	Product responseUpdatedExpected;
	ProductRequest productRequest;


	@BeforeAll
	void setUp() throws Exception
	{
		restTemplate = new RestTemplate();
		productRequest= ProductRequest.builder().productId(1).name("water").quantityInStock(199).pricePerUnit(30).build();
		responseExpected= Product.builder().productId(1).name("water").quantityInStock(199).pricePerUnit(30).build();
		responseUpdatedExpected= Product.builder().productId(1).name("water").quantityInStock(500).pricePerUnit(29.8).build();
	}

	@Test
	void addProductTest() {
		uri = "http://localhost:" + port + "/maintenance/addProduct";
		Product result = restTemplate.postForObject(uri,productRequest, Product.class);
		evaluate(responseExpected,result);
	}

	@Test
	void deleteExistingProductTest() {
		addProductTest();
		String uriDel = "http://localhost:" + port + "/maintenance/deleteProduct";
		String result = restTemplate.postForObject(uriDel,productRequest, String.class);
		assertEquals(ProductConstants.msgProductDeleted,result);
	}

	@Test
	void deleteNonExistingProductTest() {
		addProductTest();
		String uriDel = "http://localhost:" + port + "/maintenance/deleteProduct";
		ProductRequest productRequest=ProductRequest.builder().productId(20).build();
		String result = restTemplate.postForObject(uriDel,productRequest, String.class);
		assertEquals(ProductConstants.msgProductNotDeleted,result);
	}

	@Test
	void updateExistingProductTest() {
		addProductTest();
		uri = "http://localhost:" + port + "/maintenance/updateProduct";
		ProductRequest productRequestUpdated=ProductRequest.builder().productId(1).name("water").quantityInStock(500).pricePerUnit(29.8).build();
		Product result = restTemplate.postForObject(uri,productRequestUpdated, Product.class);
		evaluate(responseUpdatedExpected,result);
	}

	@Test
	void updateNonExistingProductTest() {
		addProductTest();
		uri = "http://localhost:" + port + "/maintenance/updateProduct";
		ProductRequest productRequestUpdated=ProductRequest.builder().productId(100).name("water").quantityInStock(500).pricePerUnit(29.8).build();
		Product result = restTemplate.postForObject(uri,productRequestUpdated, Product.class);
		evaluate(null,result);
	}


	private void evaluate(Product expected,Product result ){
		assertEquals(expected,result);
	}
}
