package com.chef.V1.dto;

import com.chef.V1.entity.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private List<OrderItem> items;
    private Integer people;
    private Double totalPrice;
    private String status;
    private LocalDateTime requiredByDateTime;
    private String notes;
}
