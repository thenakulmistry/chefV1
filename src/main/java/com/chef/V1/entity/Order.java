package com.chef.V1.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private ObjectId id;
    @DBRef
    private User userId;
    private Date date;
    private String status;
    @DBRef
    private List<OrderItem> items = new ArrayList<>();
    private int people;
    private int totalPrice;
}
