package com.chef.V1.controller;

import com.chef.V1.dto.OrderDTO;
import com.chef.V1.dto.UserDTO;
import com.chef.V1.entity.*;
import com.chef.V1.service.ItemService;
import com.chef.V1.service.OrderService;
import com.chef.V1.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ItemService itemService;

    @GetMapping("profile")
    public ResponseEntity<?> profile(@AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null)
            return new ResponseEntity<>(Map.of("message", "User not authenticated"), HttpStatus.UNAUTHORIZED);
        User user = userService.getByUsername(userDetails.getUsername());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("add_order")
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return new ResponseEntity<>(Map.of("message", "User not authenticated"), HttpStatus.UNAUTHORIZED);
        User user = userService.getByUsername(userDetails.getUsername());
        try{
            orderService.saveOrder(orderDTO, user.getId().toString());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("items")
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    @GetMapping("orders")
    public ResponseEntity<?> getAllOrders(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return new ResponseEntity<>(Map.of("message", "User not authenticated"), HttpStatus.UNAUTHORIZED);

        User user = userService.getByUsername(userDetails.getUsername());
        String userId = user.getId().toString();
        try{
            List<Order> orders = orderService.getAllOrders(userId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("orders/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable ObjectId orderId) {
        try{
            orderService.deleteOrderForUser(orderId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("orders/update/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable ObjectId orderId, @RequestBody OrderDTO orderDTO) {
        try{
            orderService.updateOrder(orderId, orderDTO);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody UserDTO userDTO, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return new ResponseEntity<>(Map.of("message", "User not authenticated"), HttpStatus.UNAUTHORIZED);

        try{
            userService.updateUser(userDTO, userDetails.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordRequest, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null)
            return new ResponseEntity<>(Map.of("message", "User not authenticated"), HttpStatus.UNAUTHORIZED);

        try {
            String currentPassword = passwordRequest.get("currentPassword");
            String newPassword = passwordRequest.get("newPassword");
            userService.changePassword(userDetails.getUsername(), currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}