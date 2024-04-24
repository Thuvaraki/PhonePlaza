package com.example.PhonePlaza.DTO;

import com.example.PhonePlaza.Entity.Category;
import lombok.Data;

@Data
public class ProductDTO {
    private Integer productId;
    private String productName;
    private String price;
    private String description;
    private String imageUrl;
    private Category category;
}
