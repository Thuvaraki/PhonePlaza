package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.DTO.ProductDTO;
import com.example.PhonePlaza.DTO.ProductResponseDTO;
import com.example.PhonePlaza.Entity.Product;
import com.example.PhonePlaza.Exception.ProductNotFoundException;
import com.example.PhonePlaza.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public Product AddProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setImageUrl(productDTO.getImageUrl());
        product.setCategory(productDTO.getCategory());

        return productRepository.save(product);
    }

    public List<ProductResponseDTO> ViewAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDTO> listOfProducts = new ArrayList<>();

        products.forEach(product -> {
            ProductResponseDTO responseDTO = new ProductResponseDTO();
            responseDTO.setImageUrl(product.getImageUrl());
            responseDTO.setProductName(product.getProductName());
            responseDTO.setDescription(product.getDescription());
            responseDTO.setPrice(product.getPrice());
            listOfProducts.add(responseDTO);
        });
        return listOfProducts;
    }

    public ProductResponseDTO ViewProductById(Integer productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            ProductResponseDTO responseDTO = new ProductResponseDTO();
            responseDTO.setImageUrl(product.getImageUrl());
            responseDTO.setProductName(product.getProductName());
            responseDTO.setDescription(product.getDescription());
            responseDTO.setPrice(product.getPrice());

            return responseDTO;
        } else {
            System.out.println("hi");
            throw new ProductNotFoundException("Product with ID " + productId + " not found");
        }
    }

}
