package com.chef.V1.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private ObjectId id;
    private String userId;
    private LocalDateTime createdAt;
    private String status;
    private List<OrderItem> items = new ArrayList<>();
    private Double totalPrice;
    private Integer people;
    private LocalDateTime requiredByDateTime;
    private String notes;
}
