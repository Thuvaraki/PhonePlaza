package com.example.PhonePlaza.Repository;


import com.example.PhonePlaza.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {

}
