package com.chef.V1.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Data
public class OrderItem {
    private String itemId;
    private Integer quantity;
}
