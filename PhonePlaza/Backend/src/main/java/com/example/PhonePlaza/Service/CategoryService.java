package com.example.PhonePlaza.Service;

import com.example.PhonePlaza.DTO.CategoryDTO;
import com.example.PhonePlaza.Entity.Category;
import com.example.PhonePlaza.Repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;
    public Category AddCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setCategoryId(categoryDTO.getCategoryId());
        category.setCategoryName(categoryDTO.getCategoryName());
        categoryRepository.save(category);
        return category;

    }

    public List<Category> ShowCategories() {
        return categoryRepository.findAll();
    }
}
