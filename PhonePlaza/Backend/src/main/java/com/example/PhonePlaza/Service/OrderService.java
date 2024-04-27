package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.OrderRequestDTO;
import com.example.PhonePlaza.Entity.*;
import com.example.PhonePlaza.ExceptionAndHandler.UserNotFoundException;
import com.example.PhonePlaza.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OrderService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    OrderItemRepository orderItemRepository;


    public ResponseEntity<APIResponse> createOrder(OrderRequestDTO orderReq) {
        APIResponse apiResponse = new APIResponse();
        try {
            User user = userRepository.findById(orderReq.getUserId()).orElseThrow(() -> new UserNotFoundException("user not found!"));
            Cart cart = this.cartRepository.findById(orderReq.getCartId()).orElseThrow(() -> new RuntimeException("Cart not found!"));

            if (cart.getUser().getUserId() != orderReq.getUserId()) {
                apiResponse.setStatus(HttpStatus.CONFLICT.value());
                apiResponse.setError("Invalid request to create an order!");
                return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
            }

            Order order = new Order();
            order.setUserId(orderReq.getUserId());
            order.setFirstName(orderReq.getFirstName());
            order.setLastName(orderReq.getLastName());
            order.setAddressLine1(orderReq.getAddressLine1());
            order.setAddressLine2(orderReq.getAddressLine2());
            order.setCity(orderReq.getCity());
            order.setDistrict(orderReq.getDistrict());
            order.setPhoneNo(orderReq.getPhoneNo());
            order.setPlacedOn(LocalDateTime.now());

            order = orderRepository.save(order);

            Set<OrderItem> orderItems = saveOrderItems(cart, order);
//            System.out.println("1"+orderItems);

            for (OrderItem orderItem : orderItems) {
                orderItem.setOrder(order);
            }

            orderItemRepository.saveAll(orderItems); // Save all OrderItem entities

            clearCart(cart);

            String subject = "Order verification";
            String body = "Your order has been placed";

            emailService.sendEmail(user.getEmail(), subject, body);


            apiResponse.setStatus(HttpStatus.CREATED.value());
            apiResponse.setError("Order has been successfully placed!");
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);


        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.CONFLICT.value());
            apiResponse.setError("Failed to create order: Please try again later!");
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
    }

    private Set<OrderItem> saveOrderItems(Cart cart, Order order) {
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        Set<OrderItem> orderItems = new HashSet<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProduct().getProductId());
            orderItem.setProductQuantity(cartItem.getQuantity());
            orderItem.setTotalProductPrice(cartItem.getSubTotal());
            orderItem.setOrderItemId(cartItem.getCartItemId());
            orderItems.add(orderItem);
            orderItem = orderItemRepository.save(orderItem);
        }
//        System.out.println("2"+orderItems);
        return orderItems;
    }

    private void clearCart(Cart cart) {
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        cartItemRepository.deleteAll(cartItems);
        cartRepository.delete(cart);
    }

    public ResponseEntity<APIResponse> getOrders(String email) {
        APIResponse apiResponse = new APIResponse();
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User not found!");
            }
            List<Order> orders = orderRepository.findByUserIdOrderByOrderIdDesc(user.getUserId());

            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData(orders);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);

        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
            apiResponse.setError("Failed to retrieve orders: Please try again later!");
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
    }

    public ResponseEntity<APIResponse> cancelOrder(int orderId) {
        APIResponse apiResponse = new APIResponse();
        try {
            Optional<Order> order = orderRepository.findById(orderId);

            if(!order.isPresent()){
                throw new Exception("Order Not Found");
            }

            Set<OrderItem> orderItems = order.get().getOrderItem();
            System.out.println(orderItems);
            for (OrderItem orderItem : orderItems) {
                orderItemRepository.delete(orderItem);
            }

//            orderRepository.deleteById(orderId);

            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData("Order has been successfully cancelled!");

            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        } catch (Exception e) {
            apiResponse.setStatus(HttpStatus.CONFLICT.value());
            apiResponse.setData("Failed to cancel order: Please try again later!");

            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
    }
}
