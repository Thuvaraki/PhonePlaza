package com.example.PhonePlaza.DTO;

import com.example.PhonePlaza.Entity.Category;
import lombok.Data;

@Data
public class ProductResponseDTO {
    private String imageUrl;
    private String productName;
    private String description;
    private String price;

}
