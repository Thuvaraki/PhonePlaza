package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.DTO.ProductDTO;
import com.example.PhonePlaza.DTO.ProductResponseDTO;
import com.example.PhonePlaza.Entity.Product;
import com.example.PhonePlaza.ExceptionAndHandler.ProductNotFoundException;
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
            throw new ProductNotFoundException("Product with ID " + productId + " not found");
        }
    }

    public List<ProductResponseDTO> findProductByCategory(Integer categoryId) {
        List<Product> products = productRepository.findByCategoryCategoryId(categoryId);
        List<ProductResponseDTO> productList = new ArrayList<>();

        for (Product product : products) {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setImageUrl(product.getImageUrl());
            productResponseDTO.setProductName(product.getProductName());
            productResponseDTO.setDescription(product.getDescription());
            productResponseDTO.setPrice(product.getPrice());
            productList.add(productResponseDTO);
        }

        return productList;
    }

    public ProductResponseDTO updateProduct(int productId, ProductDTO updatedProductDto) {
        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        existingProduct.setProductName(updatedProductDto.getProductName());
        existingProduct.setPrice(updatedProductDto.getPrice());
        existingProduct.setDescription(updatedProductDto.getDescription());
        existingProduct.setImageUrl(updatedProductDto.getImageUrl());

        Product updatedProduct = productRepository.save(existingProduct);

        ProductResponseDTO updatedProductResponseDTO = new ProductResponseDTO();
        updatedProductResponseDTO.setImageUrl(updatedProduct.getImageUrl());
        updatedProductResponseDTO.setProductName(updatedProduct.getProductName());
        updatedProductResponseDTO.setDescription(updatedProduct.getDescription());
        updatedProductResponseDTO.setPrice(updatedProduct.getPrice());

        return updatedProductResponseDTO;
    }

    public List<ProductResponseDTO> searchProductsByNameOrPartialProductName(String productName) {
        List<Product> products;

        if (productName != null && !productName.isEmpty()) {
            products = productRepository.findByProductNameContaining(productName);
        } else {
            throw new IllegalArgumentException("Product name or partial product name must be provided.");
        }

        List<ProductResponseDTO> productList = new ArrayList<>();

        for (Product product : products) {
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            productResponseDTO.setImageUrl(product.getImageUrl());
            productResponseDTO.setProductName(product.getProductName());
            productResponseDTO.setDescription(product.getDescription());
            productResponseDTO.setPrice(product.getPrice());
            productList.add(productResponseDTO);
        }

        return productList;
    }

}
