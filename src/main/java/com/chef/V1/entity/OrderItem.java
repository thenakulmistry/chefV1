package com.chef.V1.entity;

import lombok.Data;

@Data
public class OrderItem {
    private String itemId;
    private String name;
    private Double price;
    private Integer quantity;
}
