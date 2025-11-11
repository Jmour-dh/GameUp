package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.service.AuthorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class AuthorControllerTest {

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsList() {
        AuthorDTO dto = AuthorDTO.builder().id(1L).name("Toto").gameIds(List.of(2L)).build();
        when(authorService.getAll()).thenReturn(List.of(dto));

        ResponseEntity<List<AuthorDTO>> response = authorController.getAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(authorService, times(1)).getAll();
    }

    @Test
    void getById_returnsDto() {
        AuthorDTO dto = AuthorDTO.builder().id(2L).name("Alice").gameIds(List.of()).build();
        when(authorService.getById(2L)).thenReturn(dto);

        ResponseEntity<AuthorDTO> response = authorController.getById(2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(response.getBody().getName()).isEqualTo("Alice");
        verify(authorService).getById(2L);
    }

    @Test
    void getById_notFound_propagatesException() {
        when(authorService.getById(99L)).thenThrow(new RuntimeException("Author not found with id: 99"));

        assertThrows(RuntimeException.class, () -> authorController.getById(99L));
        verify(authorService).getById(99L);
    }

    @Test
    void create_returnsCreated_withLocation() {
        // Utilise MockHttpServletRequest pour éviter des stubbings Mockito inutiles
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/authors");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        AuthorDTO input = AuthorDTO.builder().name("New Author").gameIds(List.of(5L)).build();
        AuthorDTO created = AuthorDTO.builder().id(10L).name("New Author").gameIds(List.of(5L)).build();
        when(authorService.create(any(AuthorDTO.class))).thenReturn(created);

        ResponseEntity<AuthorDTO> response = authorController.create(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        verify(authorService).create(input);
    }

    @Test
    void update_returnsUpdated() {
        AuthorDTO input = AuthorDTO.builder().name("Updated").gameIds(List.of()).build();
        AuthorDTO updated = AuthorDTO.builder().id(5L).name("Updated").gameIds(List.of()).build();
        when(authorService.update(5L, input)).thenReturn(updated);

        ResponseEntity<AuthorDTO> response = authorController.update(5L, input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(5L);
        verify(authorService).update(5L, input);
    }

    @Test
    void delete_callsService() {
        doNothing().when(authorService).delete(7L);

        ResponseEntity<Void> response = authorController.delete(7L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(authorService).delete(7L);
    }
}
