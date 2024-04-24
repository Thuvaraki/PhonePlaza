package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Product {
    @Id
    @Column(name="product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(name="product_name")
    private String productName;

    @Column(name="product_price")
    private String price;

    @Column(name="product_description")
    private String description;

    @Column(name="product_image")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name="categoryId",referencedColumnName = "categoryId",nullable = false)
    private Category category;
}
