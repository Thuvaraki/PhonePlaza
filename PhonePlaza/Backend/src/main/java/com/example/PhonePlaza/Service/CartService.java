package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.CartItemRequestDTO;
import com.example.PhonePlaza.Entity.Cart;
import com.example.PhonePlaza.Entity.CartItem;
import com.example.PhonePlaza.Entity.Product;
import com.example.PhonePlaza.Entity.User;
import com.example.PhonePlaza.ExceptionAndHandler.UserNotFoundException;
import com.example.PhonePlaza.Repository.CartItemRepository;
import com.example.PhonePlaza.Repository.CartRepository;
import com.example.PhonePlaza.Repository.ProductRepository;
import com.example.PhonePlaza.Repository.UserRepository;
import com.example.PhonePlaza.Util.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    private JwtUtils jwtUtils;


    public ResponseEntity<APIResponse> addItemToCart(CartItemRequestDTO itemRequest, String authorizationHeader) {
        APIResponse apiResponse = new APIResponse();
        // 1. Validate JWT token presence
        if (authorizationHeader == null) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setError("Unauthorized access!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        // 2. Extract user email from JWT token
        String email = null;
        try {
            Claims claims = jwtUtils.verify(authorizationHeader);
            email = claims.get("emailId", String.class);
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setError("Unauthorized access!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
        }

        // 3. Validate user existence
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        try {
            // 4. Proceed with cart logic with improved handling
            Optional<Product> product = productRepository.findById(itemRequest.getProductId());
            if (!product.isPresent()) {
                apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
                apiResponse.setError("Product not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }

            Cart cart = cartRepository.findByUser(userRepository.findByEmail(email));
            if (cart == null) {
                cart = new Cart(userRepository.findByEmail(email));
                cartRepository.save(cart);
            }

            CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product.get());
            double amount = Double.parseDouble(String.format("%.2f", Double.parseDouble(product.get().getPrice()) * itemRequest.getQuantity()));
            if (cartItem == null) {
                cartItem = new CartItem(cart, product.get(), itemRequest.getQuantity(), amount);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + itemRequest.getQuantity());
                cartItem.setSubTotal(cartItem.getSubTotal() + amount);
            }

//            // Add the cart item to the cart's items set
//            cart.getItems().add(cartItem);
            // Update the total price of the cart
            cart.setTotalPrice(cart.getTotalPrice()+ cartItem.getSubTotal());

            // Save the updated cart and cart item
            cartRepository.save(cart);
            cartItemRepository.save(cartItem);


            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData(cartItem);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

        } catch (JwtException e) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setError("Invalid or expired token!");
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            apiResponse.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }
    }
}