package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer cartId;

    @OneToOne
    @JoinColumn(name="userId",referencedColumnName = "userId",nullable = false)
    private User user;

//   private double totalPrice;

    public Cart(User user) {
        this.user = user;
    }
}
