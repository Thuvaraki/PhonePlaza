package com.example.PhonePlaza.Controller;

import com.example.PhonePlaza.DTO.CategoryDTO;
import com.example.PhonePlaza.Entity.Category;
import com.example.PhonePlaza.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phoneplaza/categories")
@CrossOrigin
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/addCategory")
    public ResponseEntity<Category> AddCategory(@RequestBody CategoryDTO categoryDto){
        Category savedCategory= categoryService.AddCategory(categoryDto);
        return new ResponseEntity<Category>(savedCategory, HttpStatus.CREATED);

    }


    @GetMapping(path = "/viewCategory")
    public ResponseEntity<List<Category>> ShowCategories(){
        List<Category> categories = categoryService.ShowCategories();
        return new ResponseEntity<List<Category>>(categories, HttpStatus.ACCEPTED);
    }
}
