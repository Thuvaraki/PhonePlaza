package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.OrderRequestDTO;
import com.example.PhonePlaza.ExceptionAndHandler.UserNotFoundException;
import com.example.PhonePlaza.Service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/phoneplaza/user/order")
public class OrderController {
    @Autowired
    OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<APIResponse> createOrder(@RequestBody OrderRequestDTO orderReq) throws UserNotFoundException {
        return orderService.createOrder(orderReq);
    }

    @GetMapping("/getOrderItems")
    public ResponseEntity<APIResponse> getOrders(@RequestParam String email) throws UserNotFoundException {
        return orderService.getOrders(email);
    }

    @DeleteMapping("/deleteOrder/{orderId}")
    public ResponseEntity<APIResponse> cancelOrder(@PathVariable Integer orderId) {
        return orderService.cancelOrder(orderId);
    }
}
