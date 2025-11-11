package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.service.PublisherService;
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
class PublisherControllerTest {

    @Mock
    private PublisherService publisherService;

    @InjectMocks
    private PublisherController publisherController;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsList() {
        PublisherDTO dto = PublisherDTO.builder().id(1L).name("Editeur1").build();
        when(publisherService.getAll()).thenReturn(List.of(dto));

        ResponseEntity<List<PublisherDTO>> response = publisherController.getAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(publisherService, times(1)).getAll();
    }

    @Test
    void getById_returnsDto() {
        PublisherDTO dto = PublisherDTO.builder().id(2L).name("Editeur2").build();
        when(publisherService.getById(2L)).thenReturn(dto);

        ResponseEntity<PublisherDTO> response = publisherController.getById(2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(2L);
        assertThat(response.getBody().getName()).isEqualTo("Editeur2");
        verify(publisherService).getById(2L);
    }

    @Test
    void getById_notFound_propagatesException() {
        when(publisherService.getById(99L)).thenThrow(new ResourceNotFoundException("Publisher not found with id 99"));

        assertThrows(ResourceNotFoundException.class, () -> publisherController.getById(99L));
        verify(publisherService).getById(99L);
    }

    @Test
    void create_returnsCreated_withLocation() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/publishers");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PublisherDTO input = PublisherDTO.builder().name("NewPub").build();
        PublisherDTO created = PublisherDTO.builder().id(10L).name("NewPub").build();
        when(publisherService.create(any(PublisherDTO.class))).thenReturn(created);

        ResponseEntity<PublisherDTO> response = publisherController.create(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        verify(publisherService).create(input);
    }

    @Test
    void update_returnsUpdated() {
        PublisherDTO input = PublisherDTO.builder().name("Updated").build();
        PublisherDTO updated = PublisherDTO.builder().id(5L).name("Updated").build();
        when(publisherService.update(5L, input)).thenReturn(updated);

        ResponseEntity<PublisherDTO> response = publisherController.update(5L, input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(5L);
        verify(publisherService).update(5L, input);
    }

    @Test
    void delete_callsService() {
        doNothing().when(publisherService).delete(7L);

        ResponseEntity<Void> response = publisherController.delete(7L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(publisherService).delete(7L);
    }
}