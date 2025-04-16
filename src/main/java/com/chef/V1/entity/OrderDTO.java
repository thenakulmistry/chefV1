package com.chef.V1.entity;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {
    private List<OrderItem> items;
    private Integer people;
    private Integer totalPrice;
}
