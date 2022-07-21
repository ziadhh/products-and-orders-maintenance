package com.rohlik.productsandordersmaintenance;

import com.rohlik.productsandordersmaintenance.constants.OrderConstants;
import com.rohlik.productsandordersmaintenance.constants.ProductConstants;
import com.rohlik.productsandordersmaintenance.dto.OrderDTO;
import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductDTO;
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


import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

	List<ProductRequest> productRequests;
	OrderRequest orderRequest;
	OrderRequest orderRequestWrong;


	@BeforeAll
	void setUp() throws Exception
	{
		restTemplate = new RestTemplate();
		productRequest= ProductRequest.builder().productId(1).name("water").quantityInStock(199).pricePerUnit(30).build();
		responseExpected= Product.builder().productId(1).name("water").quantityInStock(199).pricePerUnit(30).build();
		responseUpdatedExpected= Product.builder().productId(1).name("water").quantityInStock(500).pricePerUnit(29.8).build();

		 productRequests=Arrays.asList(new ProductRequest[]{ProductRequest.builder().productId(1).name("bread").quantityInStock(2000).pricePerUnit(1.2).build(),
										ProductRequest.builder().productId(2).name("water").quantityInStock(2000).pricePerUnit(1.1).build(),
										ProductRequest.builder().productId(3).name("rice").quantityInStock(2000).pricePerUnit(0.9).build()});

		List<ProductDTO> productsInOrder= Arrays.asList(new ProductDTO[]{ProductDTO.builder().productId(1).quantity(200).build(),
				                                  ProductDTO.builder().productId(2).quantity(300).build(),
												  ProductDTO.builder().productId(3).quantity(400).build()});
		orderRequest= OrderRequest.builder().orderId(1).products(productsInOrder).build();

		productsInOrder= Arrays.asList(new ProductDTO[]{ProductDTO.builder().productId(1).quantity(200).build(),
				ProductDTO.builder().productId(2).quantity(3000).build(),
				ProductDTO.builder().productId(3).quantity(400).build()});
		orderRequestWrong= OrderRequest.builder().orderId(1).products(productsInOrder).build();
	}

	@Test
	void addProductTest() {
		uri = "http://localhost:" + port + "/maintenance/addProduct";
		Product result = restTemplate.postForObject(uri,productRequest, Product.class);
		evaluateProducts(responseExpected,result);
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
		evaluateProducts(responseUpdatedExpected,result);
	}

	@Test
	void updateNonExistingProductTest() {
		addProductTest();
		uri = "http://localhost:" + port + "/maintenance/updateProduct";
		ProductRequest productRequestUpdated=ProductRequest.builder().productId(100).name("water").quantityInStock(500).pricePerUnit(29.8).build();
		Product result = restTemplate.postForObject(uri,productRequestUpdated, Product.class);
		evaluateProducts(null,result);
	}


	@Test
	void addOrderWithoutMissedProductsTest() {

		String uriPorduct = "http://localhost:" + port + "/maintenance/addProduct";
		productRequests.stream().forEach(request-> {
					restTemplate.postForObject(uriPorduct, request, Product.class);
				});

		String uriOrder = "http://localhost:" + port + "/maintenance/addOrder";
		OrderDTO resultOrder = restTemplate.postForObject(uriOrder,orderRequest, OrderDTO.class);
		assertEquals(resultOrder.getStatus(), OrderConstants.Status.CREATED.name());

	}

	@Test
	void addOrderWithMissedProductsTest() {

		String uriPorduct = "http://localhost:" + port + "/maintenance/addProduct";
		productRequests.stream().forEach(request-> {
			restTemplate.postForObject(uriPorduct, request, Product.class);
		});

		String uriOrder = "http://localhost:" + port + "/maintenance/addOrder";
		OrderDTO resultOrder = restTemplate.postForObject(uriOrder,orderRequestWrong, OrderDTO.class);
		assertEquals(resultOrder.getStatus(), OrderConstants.Status.CREATED_PARTIALLY.name());


	}

	private void evaluateProducts(Product expected,Product result ){
		assertEquals(expected,result);
	}
}
