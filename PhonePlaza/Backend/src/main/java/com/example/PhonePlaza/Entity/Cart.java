package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer cartId;

    @OneToOne
    @JoinColumn(name="userId",referencedColumnName = "userId",nullable = false)
    private User user;

    private double totalPrice;
}
