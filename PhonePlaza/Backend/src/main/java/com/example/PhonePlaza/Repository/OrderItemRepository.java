package com.example.PhonePlaza.Repository;

import com.example.PhonePlaza.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository  extends JpaRepository<OrderItem,Integer>{
}
