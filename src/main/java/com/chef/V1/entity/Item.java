package com.chef.V1.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "items")
public class Item {
    @Id
    private ObjectId id;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
    private Boolean available;
}
