package com.example.PhonePlaza.Repository;

import com.example.PhonePlaza.Entity.Cart;
import com.example.PhonePlaza.Entity.CartItem;
import com.example.PhonePlaza.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {

    CartItem findByCartAndProduct(Cart cart, Product product);
}
