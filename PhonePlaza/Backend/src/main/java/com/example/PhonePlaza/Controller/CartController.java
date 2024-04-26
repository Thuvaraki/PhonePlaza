package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.CartItemRequestDTO;
import com.example.PhonePlaza.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/phoneplaza/user/cart")
public class CartController {
        @Autowired
        private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<APIResponse> addToCart(@RequestBody CartItemRequestDTO itemRequest, @RequestHeader(required = false) String authorizationHeader) {
        APIResponse apiResponse = cartService.addItemToCart(itemRequest, authorizationHeader).getBody();
        return ResponseEntity.status(HttpStatus.valueOf(apiResponse.getStatus())).body(apiResponse);
    }

    @PutMapping("/update/{cartItemId}/{newQuantity}")
    public ResponseEntity<APIResponse> updateCartItemQuantity(@PathVariable Integer cartItemId, @PathVariable int newQuantity, @RequestHeader(required = false) String authorizationHeader) {
        return cartService.updateCartItemQuantity(cartItemId, newQuantity, authorizationHeader);
    }

}
