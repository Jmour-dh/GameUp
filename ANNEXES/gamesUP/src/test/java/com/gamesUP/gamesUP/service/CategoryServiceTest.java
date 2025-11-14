package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.CategoryDTO;
import com.gamesUP.gamesUP.entity.Category;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getAll_returnsList() {
        Category c = Category.builder().id(1L).type("Action").build();
        when(categoryRepository.findAll()).thenReturn(List.of(c));

        var result = categoryService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Action", result.get(0).getType());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getById_returnsDto() {
        Category c = Category.builder().id(2L).type("RPG").build();
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(c));

        CategoryDTO dto = categoryService.getById(2L);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals("RPG", dto.getType());
        verify(categoryRepository, times(1)).findById(2L);
    }

    @Test
    void getById_notFound_throws() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getById(99L));
        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void create_savesAndReturns() {
        CategoryDTO input = CategoryDTO.builder().type("Puzzle").build();
        Category toSave = Category.builder().type("Puzzle").build();
        Category saved = Category.builder().id(10L).type("Puzzle").build();

        when(categoryRepository.save(any(Category.class))).thenReturn(saved);

        CategoryDTO created = categoryService.create(input);

        assertNotNull(created);
        assertEquals(10L, created.getId());
        assertEquals("Puzzle", created.getType());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void update_updatesAndReturns() {
        Category existing = Category.builder().id(5L).type("Old").build();
        Category updatedEntity = Category.builder().id(5L).type("Sport").build();
        CategoryDTO input = CategoryDTO.builder().type("Sport").build();

        when(categoryRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(updatedEntity);

        CategoryDTO result = categoryService.update(5L, input);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Sport", result.getType());
        verify(categoryRepository, times(1)).findById(5L);
        verify(categoryRepository, times(1)).save(existing);
    }

    @Test
    void update_notFound_throws() {
        when(categoryRepository.findById(7L)).thenReturn(Optional.empty());
        CategoryDTO input = CategoryDTO.builder().type("Any").build();

        assertThrows(ResourceNotFoundException.class, () -> categoryService.update(7L, input));
        verify(categoryRepository, times(1)).findById(7L);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void delete_callsRepositoryWhenExists() {
        when(categoryRepository.existsById(3L)).thenReturn(true);

        categoryService.delete(3L);

        verify(categoryRepository, times(1)).existsById(3L);
        verify(categoryRepository, times(1)).deleteById(3L);
    }

    @Test
    void delete_notFound_throws() {
        when(categoryRepository.existsById(4L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.delete(4L));
        verify(categoryRepository, times(1)).existsById(4L);
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
