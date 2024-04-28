package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.Common.APIResponse;
import com.example.PhonePlaza.DTO.OrderRequestDTO;
import com.example.PhonePlaza.DTO.ViewOrderResponseDTO;
import com.example.PhonePlaza.Entity.*;
import com.example.PhonePlaza.ExceptionAndHandler.UserNotFoundException;
import com.example.PhonePlaza.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    ProductRepository productRepository;

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
            orderItems.add(orderItem);
            orderItem = orderItemRepository.save(orderItem);
        }
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
            List<ViewOrderResponseDTO> responseDTOs = new ArrayList<>();

            for (Order order : orders) {
                List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(order.getOrderId());

                // Process each order item to get product details
                for (OrderItem orderItem : orderItems) {
                    Product product = productRepository.findById(orderItem.getProductId()).orElseThrow(() -> new RuntimeException("Product not found for order item"));
                    ViewOrderResponseDTO viewOrderResponseDTO = new ViewOrderResponseDTO();

                    viewOrderResponseDTO.setOrderId(order.getOrderId());
                    viewOrderResponseDTO.setUserId(order.getUserId());
                    viewOrderResponseDTO.setFirstName(order.getFirstName());
                    viewOrderResponseDTO.setLastName(order.getLastName());
                    viewOrderResponseDTO.setAddressLine1(order.getAddressLine1());
                    viewOrderResponseDTO.setAddressLine2(order.getAddressLine2());
                    viewOrderResponseDTO.setCity(order.getCity());
                    viewOrderResponseDTO.setDistrict(order.getDistrict());
                    viewOrderResponseDTO.setPhoneNo(order.getPhoneNo());
                    viewOrderResponseDTO.setPlacedOn(order.getPlacedOn());
//                    System.out.println("1 " + viewOrderResponseDTO);

                    viewOrderResponseDTO.setOrderItemId(orderItem.getOrderItemId());
                    viewOrderResponseDTO.setProductId(orderItem.getProductId());
                    viewOrderResponseDTO.setProductQuantity(orderItem.getProductQuantity());
                    viewOrderResponseDTO.setTotalProductPrice(orderItem.getTotalProductPrice());
//                    System.out.println("2 " + viewOrderResponseDTO);

                    viewOrderResponseDTO.setProductName(product.getProductName());
                    viewOrderResponseDTO.setImageUrl(product.getImageUrl());

                    responseDTOs.add(viewOrderResponseDTO);
                }

            }
            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData(responseDTOs);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
        catch (Exception e) {
            System.out.println("e");
            apiResponse.setStatus(HttpStatus.NOT_FOUND.value());
            apiResponse.setError("Failed to retrieve orders: Please try again later!");
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
    }

    public ResponseEntity<APIResponse> cancelOrder(Integer orderId) {
        APIResponse apiResponse=new APIResponse();
        try {
            Optional<Order> order = this.orderRepository.findById(orderId);
            if(!(order.isPresent())){
                throw new RuntimeException("Order not found");
            }

            List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(orderId);
//            System.out.println(orderItems);

            for(OrderItem orderItem : orderItems){
                orderItemRepository.deleteById(orderItem.getOrderItemId());
            }

            orderRepository.deleteById(orderId);

            apiResponse.setStatus(HttpStatus.OK.value());
            apiResponse.setData("Order deleted successfully!");

            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }catch(Exception e) {
            apiResponse.setStatus(HttpStatus.CONFLICT.value());
            apiResponse.setError("Can't delete the order");
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
    }
}