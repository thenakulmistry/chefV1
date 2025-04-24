package com.chef.V1.service;

import com.chef.V1.entity.Order;
import com.chef.V1.entity.OrderDTO;
import com.chef.V1.repository.OrderRepository;
import com.chef.V1.repository.OrderRepositoryImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderRepositoryImpl orderRepositoryImpl;

    public void saveOrder(OrderDTO orderDTO, String userId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setDate(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setPeople(orderDTO.getPeople());
        order.setItems(orderDTO.getItems());
        orderRepository.save(order);
    }

    public List<Order> findAll(){return orderRepository.findAll();}

    public List<Order> getAllOrders(String userId) {return orderRepositoryImpl.findAllOrders(userId);}

    public void deleteOrder(ObjectId orderId) {orderRepository.deleteById(orderId);}

    public void deleteOrderForUser(ObjectId orderId){
        Order order = orderRepository.findOrderById(orderId);
        order.setUserId(null);
    }
    public void updateOrder(ObjectId orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findOrderById(orderId);
        order.setDate(LocalDateTime.now());
        order.setItems(orderDTO.getItems());
        order.setPeople(orderDTO.getPeople());
        order.setTotalPrice(orderDTO.getTotalPrice());
        orderRepository.save(order);
    }

    public void updateOrderStatus(ObjectId orderId, String status) {
        Order oldOrder = orderRepository.findOrderById(orderId);
        oldOrder.setStatus(status);
        orderRepository.save(oldOrder);
    }
}
