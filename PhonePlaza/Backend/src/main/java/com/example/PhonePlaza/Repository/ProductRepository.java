package com.example.PhonePlaza.Repository;


import com.example.PhonePlaza.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product,Integer> {

    
    List<Product> findByCategoryCategoryId(Integer categoryId);

    List<Product> findByProductNameContaining(String productName);

}
