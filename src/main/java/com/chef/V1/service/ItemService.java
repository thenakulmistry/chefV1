package com.chef.V1.service;

import com.chef.V1.entity.Item;
import com.chef.V1.repository.ItemRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ItemService {

    @Autowired
    private ItemRepository itemRepo;

    public void addItem(Item item) {itemRepo.save(item);}

    public void deleteItem(ObjectId item) {itemRepo.deleteById(item);}

    public Optional<Item> getItemById(ObjectId itemId) {return itemRepo.findById(itemId);}

    public List<Item> getAllItems() {return itemRepo.findAll();}

    public void updateItem(ObjectId itemId, Map<String, Object> item){
        Optional<Item> old = itemRepo.findById(itemId);
        if(old.isPresent()) {
            Item oldItem = old.get();
            for (String key : item.keySet()) {
                switch (key) {
                    case "name" -> oldItem.setName(item.get(key).toString());
                    case "description" -> oldItem.setDescription(item.get(key).toString());
                    case "price" -> oldItem.setPrice((Integer) item.get(key));
                    case "available" -> oldItem.setAvailable((Boolean) item.get(key));
                    case "imageUrl" -> oldItem.setImageUrl(item.get(key).toString());
                }
            }
            itemRepo.save(oldItem);
        }
    }
}
