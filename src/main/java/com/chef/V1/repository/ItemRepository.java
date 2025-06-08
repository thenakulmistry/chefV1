package com.chef.V1.repository;

import com.chef.V1.entity.Item;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, ObjectId> {
    public Item findItemById(ObjectId id);
}
