package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.service.WishlistService;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsList() {
        WishlistDTO dto = WishlistDTO.builder().id(1L).userId(2L).gameId(3L).build();
        when(wishlistService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<WishlistDTO>> response = wishlistController.getAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(wishlistService, times(1)).findAll();
    }

    @Test
    void getById_returnsDto() {
        WishlistDTO dto = WishlistDTO.builder().id(2L).userId(4L).gameId(5L).build();
        when(wishlistService.findById(2L)).thenReturn(dto);

        ResponseEntity<WishlistDTO> response = wishlistController.getById(2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(2L);
        verify(wishlistService).findById(2L);
    }

    @Test
    void getById_notFound_propagatesException() {
        when(wishlistService.findById(99L)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Wishlist not found"));

        assertThrows(ResponseStatusException.class, () -> wishlistController.getById(99L));
        verify(wishlistService).findById(99L);
    }

    @Test
    void create_returnsCreated_withLocation() {
        // Utilise MockHttpServletRequest pour éviter des stubbings Mockito inutiles
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/wishlists");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        WishlistDTO input = WishlistDTO.builder().userId(2L).gameId(3L).build();
        WishlistDTO created = WishlistDTO.builder().id(10L).userId(2L).gameId(3L).build();
        when(wishlistService.createWishlist(any(WishlistDTO.class))).thenReturn(created);

        ResponseEntity<WishlistDTO> response = wishlistController.createWishlist(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        verify(wishlistService).createWishlist(input);
    }

    @Test
    void update_returnsUpdated() {
        WishlistDTO input = WishlistDTO.builder().userId(7L).gameId(8L).build();
        WishlistDTO updated = WishlistDTO.builder().id(5L).userId(7L).gameId(8L).build();
        when(wishlistService.update(5L, input)).thenReturn(updated);

        ResponseEntity<WishlistDTO> response = wishlistController.update(5L, input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(5L);
        verify(wishlistService).update(5L, input);
    }

    @Test
    void delete_callsService() {
        doNothing().when(wishlistService).delete(7L);

        ResponseEntity<Void> response = wishlistController.delete(7L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(wishlistService).delete(7L);
    }
}
