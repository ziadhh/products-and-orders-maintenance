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

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
        OrderDTO orderDTO= new OrderDTO();
        log.info("Order will be saved");
        OrderStatus orderStatus=new OrderStatus();
        orderStatus.setOrderId(orderRequest.getOrderId());
        orderStatus.setStatus(OrderConstants.Status.CREATED.name());
        orderStatus.setLastUpdate(LocalDateTime.now());
        for (ProductDTO prod : productsInOrder) {
            Order order = new Order();
            OrderId orderId = new OrderId(orderRequest.getOrderId(), prod.getProductId());
            order.setId(orderId);
            order.setQuantity(prod.getQuantity());
            int productId = order.getId().getProductId();
            Product product = productRepository.findById(productId);
            Integer quantityInStock = product.getQuantityInStock();
            Integer updatedQuantityInStock = quantityInStock - order.getQuantity();
            if (updatedQuantityInStock<0){
                log.warn("The order can't be done, the are "+ Math.abs(updatedQuantityInStock) +" items for product with id "+ productId+" missing");
                prod.setMissedQuantity(Math.abs(updatedQuantityInStock));
                orderStatus.setStatus(OrderConstants.Status.CREATED_PARTIALLY.name());
                missingProducts.add(prod);
            }else {
                product.setQuantityInStock(updatedQuantityInStock);
                orderRepository.save(order);
                productRepository.save(product);
                orderStatusRepository.save(orderStatus);
            }
        }
        if (missingProducts.size()>0){
            orderDTO.setOrderId(orderRequest.getOrderId());
            orderDTO.setProducts(missingProducts);
            orderDTO.setStatus("MISSING PRODUCTS");
        }else{
            orderDTO.setOrderId(orderRequest.getOrderId());
            orderDTO.setProducts(productsInOrder);
            orderDTO.setStatus(OrderConstants.Status.CREATED.name());
        }
        log.info("order saved");
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


    @Scheduled(cron = "${expiry.unpaid.order.check:0 */2 * * * *}")
    public void cancelUnpaidOrderes() {
        log.warn("Checking unpaid orderders... ");
        List<OrderStatus> orderStatus=orderStatusRepository.findUnpaidOrders(OrderConstants.Status.CREATED.name());
        orderStatus.stream().forEach(ord->{
            log.info("The order "+ord.getOrderId()+" has been cancelled");
            cancelOrder(ord.getOrderId(),false);
        });

    }


}
