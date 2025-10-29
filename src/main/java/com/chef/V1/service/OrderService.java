package com.chef.V1.service;

import com.chef.V1.entity.Order;
import com.chef.V1.dto.OrderDTO;
import com.chef.V1.dto.OrderViewDTO;
import com.chef.V1.entity.Item;
import com.chef.V1.entity.OrderItem;
import com.chef.V1.entity.User;
import com.chef.V1.repository.OrderRepository;
import com.chef.V1.repository.OrderRepositoryImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderRepositoryImpl orderRepositoryImpl;
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;

    public void saveOrder(OrderDTO orderDTO, String userId) {
        double serverCalculatedTotalPrice = 0.0;
        for (OrderItem orderItem : orderDTO.getItems()) {
            Optional<Item> dbItemOpt = itemService.getItemById(new ObjectId(orderItem.getItemId()));
            if (dbItemOpt.isPresent()) {
                Item dbItem = dbItemOpt.get();
                serverCalculatedTotalPrice += dbItem.getPrice() * orderItem.getQuantity();
                // Also ensure the name and price in the order item match the database
                orderItem.setName(dbItem.getName());
                orderItem.setPrice(dbItem.getPrice());
            } else {
                throw new RuntimeException("Attempted to order an invalid item: " + orderItem.getItemId());
            }
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setTotalPrice(serverCalculatedTotalPrice);
        order.setPeople(orderDTO.getPeople());
        order.setItems(orderDTO.getItems());
        order.setRequiredByDateTime(orderDTO.getRequiredByDateTime());
        order.setNotes(orderDTO.getNotes());
        orderRepository.save(order);
    }

    public List<OrderViewDTO> findAllWithUserDetails(){ // Renamed for clarity, or replace findAll
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(order -> {
            User user = null;
            if (order.getUserId() != null && !order.getUserId().isEmpty()) {
                try {
                    user = userService.findById(new ObjectId(order.getUserId()));
                } catch (IllegalArgumentException e) {
                    // Handle cases where userId might not be a valid ObjectId string
                    System.err.println("Invalid ObjectId string for userId: " + order.getUserId());
                }
            }
            return new OrderViewDTO(order, user != null ? user.getUsername() : "N/A");
        }).collect(Collectors.toList());
    }

    public List<Order> getAllOrders(String userId) {return orderRepositoryImpl.findAllOrders(userId);}

    public void deleteOrder(ObjectId orderId) {orderRepository.deleteById(orderId);}

    public void deleteOrderForUser(ObjectId orderId){
        Order order = orderRepository.findOrderById(orderId);
        order.setUserId(null);
    }
    public void updateOrder(ObjectId orderId, OrderDTO orderDTO) {
        Order order = orderRepository.findOrderById(orderId);
        order.setItems(orderDTO.getItems());
        order.setPeople(orderDTO.getPeople());
        order.setTotalPrice(orderDTO.getTotalPrice());
        order.setStatus(orderDTO.getStatus());
        order.setRequiredByDateTime(orderDTO.getRequiredByDateTime());
        order.setNotes(orderDTO.getNotes());
        orderRepository.save(order);
    }
}
