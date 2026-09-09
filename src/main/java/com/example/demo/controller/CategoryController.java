package com.example.demo.controller;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/all")
    public List<CategoryDTO> getAllUsers(
        @RequestParam(defaultValue = "10") int limit,
        @RequestParam(defaultValue = "0") int offset
    ) {
        return this.categoryService.getAllCategory(limit, offset);
    }
}
