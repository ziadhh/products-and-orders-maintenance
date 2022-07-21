package com.rohlik.productsandordersmaintenance.service;


import com.rohlik.productsandordersmaintenance.constants.OrderConstants;
import com.rohlik.productsandordersmaintenance.dto.OrderDTO;
import com.rohlik.productsandordersmaintenance.dto.OrderRequest;
import com.rohlik.productsandordersmaintenance.dto.ProductDTO;
import com.rohlik.productsandordersmaintenance.entity.Order;
import com.rohlik.productsandordersmaintenance.entity.OrderId;
import com.rohlik.productsandordersmaintenance.entity.OrderStatus;
import com.rohlik.productsandordersmaintenance.entity.Product;
import com.rohlik.productsandordersmaintenance.repository.OrderRepository;
import com.rohlik.productsandordersmaintenance.repository.OrderStatusRepository;
import com.rohlik.productsandordersmaintenance.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderStatusRepository orderStatusRepository;


    @Transactional(rollbackFor = SQLException.class)
    public OrderDTO saveOrder(OrderRequest orderRequest) {

        List<ProductDTO> productsInOrder = orderRequest.getProducts();
        List<ProductDTO> missingProducts= new ArrayList<>();
        List<ProductDTO> availabeProducts= new ArrayList<>();
        log.info("Order will be saved");
        for (ProductDTO prod : productsInOrder) {
            OrderId orderId = new OrderId(orderRequest.getOrderId(), prod.getProductId());
            Order order = Order.builder().id(orderId).quantity(prod.getQuantity()).build();
            int productId = order.getId().getProductId();
            Product product = productRepository.findById(productId);
            Integer quantityInStock = product.getQuantityInStock();
            Integer updatedQuantityInStock = quantityInStock - order.getQuantity();
            if (updatedQuantityInStock<0){
                log.warn("Product "+productId+" not added in the order "+orderRequest.getOrderId() + " There are "+Math.abs(updatedQuantityInStock) +" items  missing");
                prod.setMissedQuantity(Math.abs(updatedQuantityInStock));
                missingProducts.add(prod);
            }else {
                availabeProducts.add(prod);
                product.setQuantityInStock(updatedQuantityInStock);
                orderRepository.save(order);
                productRepository.save(product);
            }
        }

        String status=missingProducts.size()==0?OrderConstants.Status.CREATED.name():missingProducts.size()==productsInOrder.size()?OrderConstants.Status.NOT_CREATED.name():OrderConstants.Status.CREATED_PARTIALLY.name();
        OrderStatus orderStatus=OrderStatus.builder().
                                         status(status).
                                         orderId(orderRequest.getOrderId()).
                                         lastUpdate(LocalDateTime.now()).build();
        orderStatusRepository.save(orderStatus);

        OrderDTO orderDTO=OrderDTO.builder()
                .missingProducts(missingProducts)
                .orderId(orderRequest.getOrderId())
                .products(availabeProducts)
                .status(status)
                .build();

        return orderDTO;

    }



    @Transactional(rollbackFor = SQLException.class)
    public OrderStatus payOrder(Integer id) {
        OrderStatus orderStatus=orderStatusRepository.findByOrderId(id);
        if (orderStatus!=null&&(orderStatus.getStatus().equals(OrderConstants.Status.CREATED.name())||orderStatus.getStatus().equals(OrderConstants.Status.CREATED_PARTIALLY.name()))) {
            orderStatus.setOrderId(id);
            orderStatus.setStatus(OrderConstants.Status.PAID.name());
            orderStatusRepository.save(orderStatus);
        }
        return orderStatus;
    }

    @Transactional(rollbackFor = SQLException.class)
    public OrderStatus cancelOrder(Integer id,boolean manualCancel) {
        OrderStatus orderStatus=orderStatusRepository.findByOrderId(id);
        if (orderStatus!=null&&(orderStatus.getStatus().equals(OrderConstants.Status.CREATED.name())||orderStatus.getStatus().equals(OrderConstants.Status.CREATED_PARTIALLY.name()))) {
            orderStatus.setOrderId(id);
            orderStatus.setStatus(manualCancel?OrderConstants.Status.CANCELLED.name():OrderConstants.Status.INVALID.name());
            List<Order> orders = orderRepository.findByOrderId(id);
            orderStatusRepository.findById(id);
            orders.stream().forEach(ord -> {
                int orderQuantity = ord.getQuantity();
                int productId = ord.getId().getProductId();
                Product product = productRepository.findById(productId);
                Integer quantityInStock = product.getQuantityInStock();
                Integer updatedQuantityInStock = quantityInStock + orderQuantity;
                product.setQuantityInStock(updatedQuantityInStock);
                productRepository.save(product);
            });
            orderStatusRepository.save(orderStatus);
        }
        return orderStatus;
    }


    @Scheduled(cron = "${expiry.unpaid.order.check:0 */30 * * * *}")
    public void cancelUnpaidOrderes() {
        log.warn("Checking unpaid orderders... ");
        List<String> statusCreated= Arrays.asList(OrderConstants.Status.CREATED_PARTIALLY.name(),OrderConstants.Status.CREATED.name());
        List<OrderStatus> orderStatus=orderStatusRepository.findCreatedOrders(statusCreated);
        orderStatus.stream().forEach(ord->{
            log.info("The order "+ord.getOrderId()+" has been cancelled");
            cancelOrder(ord.getOrderId(),false);
        });

    }


}
