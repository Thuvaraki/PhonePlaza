package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="categoryId")
    private Integer categoryId;


    @Column(name="categoryName")
    private String categoryName;

}
