package com.chef.V1.controller;

import com.chef.V1.entity.Item;
import com.chef.V1.service.ItemService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.getAllItems(), HttpStatus.OK);
    }
    @GetMapping("add_item")
    public ResponseEntity<?> addItem(@RequestBody Item item){
        try{
            itemService.addItem(item);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteItem(@PathVariable ObjectId id){
        try {
            itemService.deleteItem(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateItem(@PathVariable ObjectId id, @RequestBody Map<String, Object> item){
        Optional<Item> old = itemService.getItemById(id);
        if(old.isPresent()){
            Item oldItem = old.get();
            for(String key : item.keySet()){
                switch (key){
                    case "name" -> oldItem.setName(item.get(key).toString());
                    case "description" -> oldItem.setDescription(item.get(key).toString());
                    case "price" -> oldItem.setPrice((Integer) item.get(key));
                    case "available" -> oldItem.setAvailable((Boolean) item.get(key));
                    case "imageUrl" -> oldItem.setImageUrl(item.get(key).toString());
                }
            }
            itemService.addItem(oldItem);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
