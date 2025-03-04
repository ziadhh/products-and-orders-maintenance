package com.rohlik.productsandordersmaintenance;

import com.rohlik.productsandordersmaintenance.constants.OrderConstants;
import com.rohlik.productsandordersmaintenance.constants.ProductConstants;
import com.rohlik.productsandordersmaintenance.dto.OrderDTO;
import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductDTO;
import com.rohlik.productsandordersmaintenance.dto.ProductRequest;
import com.rohlik.productsandordersmaintenance.entity.OrderStatus;
import com.rohlik.productsandordersmaintenance.entity.Product;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
	OrderRequest orderRequestPartiallyWrong;
    OrderRequest orderRequestCompleltlyWrong;


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
        orderRequestPartiallyWrong= OrderRequest.builder().orderId(1).products(productsInOrder).build();

        productsInOrder= Arrays.asList(new ProductDTO[]{ProductDTO.builder().productId(1).quantity(20000).build(),
                ProductDTO.builder().productId(2).quantity(30000).build(),
                ProductDTO.builder().productId(3).quantity(40000).build()});
        orderRequestCompleltlyWrong= OrderRequest.builder().orderId(1).products(productsInOrder).build();
	}

	@Test
	@Order(1)
	void addProductTest() {
		uri = "http://localhost:" + port + "/maintenance/addProduct";
		Product result = restTemplate.postForObject(uri,productRequest, Product.class);
		evaluateProducts(responseExpected,result);
	}

	@Test
	@Order(2)
	void deleteExistingProductTest() {
		addProductTest();
		String uriDel = "http://localhost:" + port + "/maintenance/deleteProduct";
		String result = restTemplate.postForObject(uriDel,productRequest, String.class);
		assertEquals(ProductConstants.msgProductDeleted,result);
	}

	@Test
	@Order(3)
	void deleteNonExistingProductTest() {
		addProductTest();
		String uriDel = "http://localhost:" + port + "/maintenance/deleteProduct";
		ProductRequest productRequest=ProductRequest.builder().productId(20).build();
		String result = restTemplate.postForObject(uriDel,productRequest, String.class);
		assertEquals(ProductConstants.msgProductNotDeleted,result);
	}

	@Test
	@Order(4)
	void updateExistingProductTest() {
		addProductTest();
		uri = "http://localhost:" + port + "/maintenance/updateProduct";
		ProductRequest productRequestUpdated=ProductRequest.builder().productId(1).name("water").quantityInStock(500).pricePerUnit(29.8).build();
		Product result = restTemplate.postForObject(uri,productRequestUpdated, Product.class);
		evaluateProducts(responseUpdatedExpected,result);
	}

	@Test
	@Order(5)
	void updateNonExistingProductTest() {
		addProductTest();
		uri = "http://localhost:" + port + "/maintenance/updateProduct";
		ProductRequest productRequestUpdated=ProductRequest.builder().productId(100).name("water").quantityInStock(500).pricePerUnit(29.8).build();
		Product result = restTemplate.postForObject(uri,productRequestUpdated, Product.class);
		evaluateProducts(null,result);
	}


	@Test
	@Order(6)
	void addOrderWithoutMissedProductsTest() {

		String uriPorduct = "http://localhost:" + port + "/maintenance/addProduct";
		productRequests.stream().forEach(request-> {
					restTemplate.postForObject(uriPorduct, request, Product.class);
				});

		String uriOrder = "http://localhost:" + port + "/maintenance/addOrder";
		OrderDTO resultOrder = restTemplate.postForObject(uriOrder,orderRequest, OrderDTO.class);
		assertEquals(resultOrder.getStatus(), OrderConstants.Status.CREATED.name());
        assertEquals(resultOrder.getMissingProducts().size(), 0);

	}

	@Test
	@Order(7)
	void addOrderWithMissedProductsTest() {

		String uriPorduct = "http://localhost:" + port + "/maintenance/addProduct";
		productRequests.stream().forEach(request-> {
			restTemplate.postForObject(uriPorduct, request, Product.class);
		});

		String uriOrder = "http://localhost:" + port + "/maintenance/addOrder";
		OrderDTO resultOrder = restTemplate.postForObject(uriOrder,orderRequestPartiallyWrong, OrderDTO.class);
		assertEquals(resultOrder.getStatus(), OrderConstants.Status.CREATED_PARTIALLY.name());
        assertEquals(resultOrder.getMissingProducts().size(), 1);

	}

    @Test
	@Order(8)
    void addOrderWithAllMissedProductsTest() {

        String uriPorduct = "http://localhost:" + port + "/maintenance/addProduct";
        productRequests.stream().forEach(request-> {
            restTemplate.postForObject(uriPorduct, request, Product.class);
        });

        String uriOrder = "http://localhost:" + port + "/maintenance/addOrder";
        OrderDTO resultOrder = restTemplate.postForObject(uriOrder,orderRequestCompleltlyWrong, OrderDTO.class);
        assertEquals(resultOrder.getStatus(), OrderConstants.Status.NOT_CREATED.name());
        assertEquals(resultOrder.getMissingProducts().size(), productRequests.size());

    }



    @Test
	@Order(9)
	void payOrderTest() {
		addOrderWithoutMissedProductsTest();
		String uriOrder = "http://localhost:" + port + "/maintenance/payOrder/"+orderRequest.getOrderId();
		OrderStatus resultOrder = restTemplate.postForObject(uriOrder,orderRequest, OrderStatus.class);
		assertEquals(resultOrder.getStatus(), OrderConstants.Status.PAID.name());
	}

	@Test
	@Order(10)
	void cancelOrderTest() {
		addOrderWithoutMissedProductsTest();
		String uriOrder = "http://localhost:" + port + "/maintenance/cancelOrder/"+orderRequest.getOrderId();
		OrderStatus resultOrder = restTemplate.postForObject(uriOrder,orderRequest, OrderStatus.class);
		assertEquals(resultOrder.getStatus(), OrderConstants.Status.CANCELLED.name());
	}

	@Test
	@Order(11)
	void deleteProductWithAsociatedOrderest() {
		addProductTest();
		String uriDel = "http://localhost:" + port + "/maintenance/deleteProduct";
		String result = restTemplate.postForObject(uriDel,productRequest, String.class);
		assertEquals(ProductConstants.msgProductNotDeleted,result);
	}


	private void evaluateProducts(Product expected,Product result ){
		assertEquals(expected,result);
	}
}
