package com.chef.V1.service;

import com.chef.V1.entity.Order;
import com.chef.V1.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public void saveOrder(Order order) {orderRepository.save(order);}
}
