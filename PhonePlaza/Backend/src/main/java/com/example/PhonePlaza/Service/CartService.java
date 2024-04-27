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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

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

    private User getAuthenticatedUser(String authorizationHeader, APIResponse apiResponse) {
        // 1. Validate JWT token presence
        if (authorizationHeader == null) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setError("Unauthorized access!");
            return null;
        }

        // 2. Extract user email from JWT token
        String email = null;
        try {
            Claims claims = jwtUtils.verify(authorizationHeader);
            email = claims.get("emailId", String.class);
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            apiResponse.setError("Unauthorized access!");
            return null;
        }

        // 3. Validate user existence
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        return user;
    }

    public ResponseEntity<APIResponse> addItemToCart(CartItemRequestDTO itemRequest, String authorizationHeader) {
        APIResponse apiResponse = new APIResponse();
        User user = getAuthenticatedUser(authorizationHeader, apiResponse);

        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        try {
            Optional<Product> product = productRepository.findById(itemRequest.getProductId());
            if (!product.isPresent()) {
                apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
                apiResponse.setError("Product not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }

            Cart cart = cartRepository.findByUser(user);
            if (cart == null) {
                cart = new Cart(user);
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


            cartItemRepository.save(cartItem);

            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData(cartItem);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            apiResponse.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }
    }

    public ResponseEntity<APIResponse> updateCartItemQuantity(Integer cartItemId, Integer newQuantity, String authorizationHeader) {
        APIResponse apiResponse = new APIResponse();
        User user = getAuthenticatedUser(authorizationHeader, apiResponse);

        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        // 2. Retrieve CartItem
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItemId);
        if (!cartItemOptional.isPresent()) {
            apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
            apiResponse.setError("Item not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }
        CartItem cartItem = cartItemOptional.get();

        if (newQuantity <= 0) {
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            apiResponse.setError("Invalid quantity! Quantity must be greater than 0.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }

        cartItem.setQuantity(newQuantity);
        double newSubTotal = Double.parseDouble(String.format("%.2f", Double.parseDouble(cartItem.getProduct().getPrice()) * newQuantity));

        cartItem.setSubTotal(newSubTotal);
        cartItemRepository.save(cartItem);

        apiResponse.setStatus(HttpStatus.OK.value());
        apiResponse.setData(cartItem);
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }


    public ResponseEntity<APIResponse> getCartItems(String authorizationHeader) {
        APIResponse apiResponse = new APIResponse();
        User user = getAuthenticatedUser(authorizationHeader, apiResponse);

        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        try {
            Cart cart = cartRepository.findByUser(user);
            if (cart == null) {
                apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
                apiResponse.setError("Cart not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }

            List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

            List<Map<String, Object>> cartItemsList = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                Product product = cartItem.getProduct();
                Map<String, Object> cartItemDetails = new HashMap<>();
                cartItemDetails.put("imageUrl", product.getImageUrl());
                cartItemDetails.put("productName", product.getProductName());
                cartItemDetails.put("price", product.getPrice());
                cartItemDetails.put("quantity", cartItem.getQuantity());
                cartItemDetails.put("subtotal", cartItem.getSubTotal());
                cartItemsList.add(cartItemDetails);
            }

            // 4. Set response data and status
            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData(cartItemsList);
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            apiResponse.setError(e.getMessage());
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        }
    }


    public ResponseEntity<APIResponse> deleteCartItems(Integer cartItemId, String authorizationHeader) {
        APIResponse apiResponse = new APIResponse();
        User user = getAuthenticatedUser(authorizationHeader, apiResponse);

        if (user == null) {
            throw new UserNotFoundException("User not found!");
        }

        try {
            Cart fetchedCart = cartRepository.findByUser(user);
            if (fetchedCart == null) {
                apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
                apiResponse.setError("Cart not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }


            Optional<CartItem> fetchedCartItemOptional = cartItemRepository.findById(cartItemId);
            if (!fetchedCartItemOptional.isPresent()) {
                apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
                apiResponse.setError("Cart item not found!");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
            }
            CartItem fetchedCartItem = fetchedCartItemOptional.get();

            if (!fetchedCartItem.getCart().getCartId().equals(fetchedCart.getCartId())) {
                apiResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                apiResponse.setError("Unauthorized !");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
            }

            cartItemRepository.deleteById(cartItemId);
            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData("Item deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (EntityNotFoundException e) {
            apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
            apiResponse.setError(String.format("Cart item with ID [%d] not found.", cartItemId));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            apiResponse.setError("An error occurred while deleting the item. Please try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
