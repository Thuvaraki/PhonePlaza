package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.DTO.ProductDTO;
import com.example.PhonePlaza.DTO.ProductResponseDTO;
import com.example.PhonePlaza.Entity.Category;
import com.example.PhonePlaza.Entity.Product;
import com.example.PhonePlaza.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phoneplaza/products")
@CrossOrigin
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/addProduct")
    public ResponseEntity<Product> AddProduct(@RequestBody ProductDTO productDTO){
        Product savedProduct= productService.AddProduct(productDTO);
        return new ResponseEntity<Product>(savedProduct, HttpStatus.CREATED);

    }

    @GetMapping(path = "/viewAllProducts")
    public ResponseEntity<List<ProductResponseDTO>> ViewAllProducts(){
        List<ProductResponseDTO> products = productService.ViewAllProducts();
        return new ResponseEntity<List<ProductResponseDTO>>(products, HttpStatus.ACCEPTED);
    }

    @GetMapping(path = "/viewProductById/{productId}")
    public ResponseEntity<ProductResponseDTO> ViewProductById(@PathVariable("productId") Integer productId){
        ProductResponseDTO product = productService.ViewProductById(productId);
        return new ResponseEntity<ProductResponseDTO>(product, HttpStatus.ACCEPTED);
    }




    //View products by category
    //Update product by id
    //delete product by id
//searchProductsByCategoryAndName
}
