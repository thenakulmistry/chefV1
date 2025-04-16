package com.chef.V1.controller;

import com.chef.V1.entity.*;
import com.chef.V1.service.OrderService;
import com.chef.V1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @PostMapping("add_order")
    public ResponseEntity<?> addOrder(@RequestBody OrderDTO orderDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByUsername(username);
        try{
            Order order = new Order();
            order.setUserId(user.getId().toString());
            order.setDate(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setTotalPrice(orderDTO.getTotalPrice());
            order.setPeople(orderDTO.getPeople());
            order.setItems(orderDTO.getItems());
            orderService.saveOrder(order);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

//    @GetMapping("orders")
//    public ResponseEntity<?> getOrders() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        try{
//            List<Order> orders = userService.getOrders(username);
//            return new ResponseEntity<>(orders, HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
