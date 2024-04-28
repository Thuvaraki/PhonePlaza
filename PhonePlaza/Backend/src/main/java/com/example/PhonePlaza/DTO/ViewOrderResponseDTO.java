package com.example.PhonePlaza.DTO;

import com.example.PhonePlaza.Entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewOrderResponseDTO {
    private int orderId;

    private long userId;

    private String firstName;

    private String lastName;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String district;

    private String phoneNo;

    private LocalDateTime placedOn;

    private Integer orderItemId;

    private int productId;

    private String productName;

    private int productQuantity;

    private String imageUrl;

    private double totalProductPrice;

}
