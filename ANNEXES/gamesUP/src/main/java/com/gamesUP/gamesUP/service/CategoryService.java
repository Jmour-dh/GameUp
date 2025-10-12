package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.CategoryDTO;
import com.gamesUP.gamesUP.entity.Category;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryDTO create(CategoryDTO dto) {
        Category entity = toEntity(dto);
        entity.setId(null);
        Category saved = categoryRepository.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));
        return toDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDTO update(Long id, CategoryDTO dto) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + id));

        existing.setType(dto.getType());

        Category updated = categoryRepository.save(existing);
        return toDto(updated);
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id " + id);
        }
        categoryRepository.deleteById(id);
    }

    private CategoryDTO toDto(Category c) {
        if (c == null) return null;
        return CategoryDTO.builder()
                .id(c.getId())
                .type(c.getType())
                .build();
    }

    private Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;
        Category c = new Category();
        c.setId(dto.getId());
        c.setType(dto.getType());
        return c;
    }
}
