package com.example.demo.service;

import com.example.demo.dto.CategoryDTO;
import com.example.demo.repository.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> getAllCategory(int limit, int offset) {
        Sort sort = Sort.by(
                Sort.Direction.fromString("asc"),
                "name"
        );

        Pageable pageable = PageRequest.of(
                offset / limit,
                limit,
                sort
        );

        return this.categoryRepository.findAll(pageable).stream().map(c ->
            new CategoryDTO(c.getName(), c.getDescription())
        ).toList();
    }
}
