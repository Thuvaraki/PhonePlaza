package com.example.PhonePlaza.Repository;

import com.example.PhonePlaza.Entity.Cart;
import com.example.PhonePlaza.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Integer> {
    Cart findByUser(User user);

//    Cart findByUserId(Long userId);
}
