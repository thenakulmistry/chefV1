package com.chef.V1.dto;

import com.chef.V1.entity.ItemType;
import lombok.Data;

@Data
public class ItemDTO {
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available;
    private ItemType itemType;
}
