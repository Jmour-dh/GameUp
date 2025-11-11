// language: java
package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.CategoryDTO;
import com.gamesUP.gamesUP.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void getAll_returnsList() {
        CategoryDTO dto = CategoryDTO.builder().id(1L).type("Action").build();
        when(categoryService.getAll()).thenReturn(List.of(dto));

        var response = categoryController.getAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void getById_returnsDto() {
        CategoryDTO dto = CategoryDTO.builder().id(2L).type("RPG").build();
        when(categoryService.getById(2L)).thenReturn(dto);

        var response = categoryController.getById(2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(response.getBody().getType()).isEqualTo("RPG");
        verify(categoryService).getById(2L);
    }

    @Test
    void getById_notFound_propagatesException() {
        when(categoryService.getById(99L)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> categoryController.getById(99L));
        verify(categoryService).getById(99L);
    }

    @Test
    void create_returnsCreated() {
        CategoryDTO input = CategoryDTO.builder().type("Puzzle").build();
        CategoryDTO created = CategoryDTO.builder().id(10L).type("Puzzle").build();
        when(categoryService.create(input)).thenReturn(created);

        var response = categoryController.create(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        verify(categoryService).create(input);
    }

    @Test
    void update_returnsUpdated() {
        CategoryDTO input = CategoryDTO.builder().type("Sport").build();
        CategoryDTO updated = CategoryDTO.builder().id(5L).type("Sport").build();
        when(categoryService.update(5L, input)).thenReturn(updated);

        var response = categoryController.update(5L, input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(5L);
        verify(categoryService).update(5L, input);
    }

    @Test
    void delete_callsService() {
        doNothing().when(categoryService).delete(7L);

        var response = categoryController.delete(7L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(categoryService).delete(7L);
    }
}
