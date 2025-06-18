package com.chef.V1.dto;

import com.chef.V1.entity.Order;
import com.chef.V1.entity.OrderItem;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderViewDTO {
    private ObjectId id;
    private String userId;
    private String username; // New field
    private LocalDateTime createdAt;
    private String status;
    private List<OrderItem> items;
    private Double totalPrice;
    private Integer people;

    // Constructor to map from Order entity and add username
    public OrderViewDTO(Order order, String username) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.username = username;
        this.createdAt = order.getCreatedAt();
        this.status = order.getStatus();
        this.items = order.getItems();
        this.totalPrice = order.getTotalPrice();
        this.people = order.getPeople();
    }

    // Optional: A constructor if username is not found or for other cases
    public OrderViewDTO(Order order) {
        this.id = order.getId();
        this.userId = order.getUserId();
        this.username = "N/A"; // Default if user not found
        this.createdAt = order.getCreatedAt();
        this.status = order.getStatus();
        this.items = order.getItems();
        this.totalPrice = order.getTotalPrice();
        this.people = order.getPeople();
    }
}
