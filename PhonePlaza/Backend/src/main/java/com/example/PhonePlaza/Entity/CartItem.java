package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer CartItemId;

    @ManyToOne
    @JoinColumn(name="cartId",referencedColumnName = "cartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "productId",referencedColumnName = "product_id")
    private Product product;

    private Integer quantity;

    private Double subTotal;

    public CartItem(Cart cart, Product product, Integer quantity, Double subTotal) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }
}
