package com.chef.V1.controller;

import com.chef.V1.entity.Item;
import com.chef.V1.service.ItemService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("user/payment")
public class PaymentController {

    @Autowired
    private ItemService itemService;

    @Setter
    @Getter
    public static class CartItemRequest {
        private String itemId;
        private int quantity;
    }

    @Setter
    @Getter
    public static class CreatePaymentRequest{
        private List<CartItemRequest> items;
    }

    private long calculateOrderAmount(List<CartItemRequest> items) {
        double totalAmount = 0.0;
        for (CartItemRequest reqItem : items) {
            Optional<Item> dbItemOpt = itemService.getItemById(new ObjectId(reqItem.getItemId()));
            if (dbItemOpt.isPresent()) {
                Item dbItem = dbItemOpt.get();
                totalAmount += dbItem.getPrice() * reqItem.getQuantity();
            } else {
                throw new RuntimeException("Invalid item in cart: " + reqItem.getItemId());
            }
        }
        // Convert total from rupees (double) to paise (long) for Stripe
        return (long) (totalAmount * 100);
    }

    @RequestMapping("create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody CreatePaymentRequest request){
        long orderAmount = calculateOrderAmount(request.getItems());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(orderAmount)
                .setCurrency("inr")
                // Use automatic_payment_methods to allow Stripe to manage payment methods from the dashboard
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();
        try{
            PaymentIntent paymentIntent = PaymentIntent.create(params);
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            return ResponseEntity.ok(response);
        }
        catch(StripeException e){
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
