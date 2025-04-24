package com.chef.V1.controller;

import com.chef.V1.entity.Item;
import com.chef.V1.entity.Order;
import com.chef.V1.entity.OrderDTO;
import com.chef.V1.entity.UserDTO;
import com.chef.V1.service.ItemService;
import com.chef.V1.service.OrderService;
import com.chef.V1.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @PostMapping("add_admin")
    public ResponseEntity<?> addAdmin(@RequestBody UserDTO user) {
        try{
            userService.addNewAdmin(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("users")
    public ResponseEntity<?> getUsers() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("orders")
    public ResponseEntity<?> getOrders() {
        return new ResponseEntity<>(orderService.findAll(), HttpStatus.OK);
    }
    @GetMapping("items")
    public ResponseEntity<List<Item>> getItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    @PostMapping("item")
    public ResponseEntity<?> addItem(@RequestBody Item item){
        try{
            itemService.addItem(item);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("item/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable ObjectId id){
        try {
            itemService.deleteItem(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("item/{id}")
    public ResponseEntity<?> updateItem(@PathVariable ObjectId id, @RequestBody Map<String, Object> item){
        try{
            itemService.updateItem(id, item);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("user/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username){
        try{
            userService.deleteByUsername(username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("user/{username}")
    public ResponseEntity<?> updateUser(@PathVariable String username, @RequestBody UserDTO userDTO){
        try{
            userService.updateUser(userDTO, username);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("order/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable ObjectId orderId, @RequestBody String status){
        try{
            orderService.updateOrderStatus(orderId, status);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
