package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="cartId",referencedColumnName = "cartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productId",referencedColumnName = "product_id")
    private Product product;

    private Integer quantity;

    private Integer subTotal;



}
