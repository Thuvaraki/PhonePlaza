package com.example.PhonePlaza.Repository;

import com.example.PhonePlaza.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Integer> {


    List<Order> findByUserIdOrderByOrderIdDesc(Long userId);
}
