package com.example.PhonePlaza.Repository;

import com.example.PhonePlaza.Entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository  extends JpaRepository<OrderItem,Integer>{

    List<OrderItem> findByOrderOrderId(Integer orderId);

    void deleteByOrderOrderId(Integer orderId);

    void deleteAllByOrderOrderId(Integer orderId);
}
