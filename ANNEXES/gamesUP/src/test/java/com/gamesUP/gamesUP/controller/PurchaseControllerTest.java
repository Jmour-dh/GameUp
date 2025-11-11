package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseControllerTest {

    @Mock
    private PurchaseService purchaseService;

    @InjectMocks
    private PurchaseController purchaseController;

    @Test
    void getAll_returnsList() {
        PurchaseDTO dto = PurchaseDTO.builder().id(1L).date(new Date()).lineIds(List.of(1L)).build();
        when(purchaseService.findAll()).thenReturn(List.of(dto));

        var response = purchaseController.getAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(purchaseService, times(1)).findAll();
    }

    @Test
    void getById_returnsDto() {
        PurchaseDTO dto = PurchaseDTO.builder().id(2L).date(new Date()).lineIds(List.of()).build();
        when(purchaseService.findById(2L)).thenReturn(dto);

        var response = purchaseController.getById(2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(2L);
        verify(purchaseService).findById(2L);
    }

    @Test
    void getById_notFound_propagatesException() {
        when(purchaseService.findById(99L)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Purchase not found"));

        assertThrows(ResponseStatusException.class, () -> purchaseController.getById(99L));
        verify(purchaseService).findById(99L);
    }

    @Test
    void create_returnsCreated() {
        PurchaseDTO input = PurchaseDTO.builder().date(new Date()).lineIds(List.of()).build();
        PurchaseDTO created = PurchaseDTO.builder().id(10L).date(input.getDate()).lineIds(List.of()).build();
        when(purchaseService.create(input)).thenReturn(created);

        var response = purchaseController.create(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        verify(purchaseService).create(input);
    }

    @Test
    void update_returnsUpdated() {
        PurchaseDTO input = PurchaseDTO.builder().date(new Date()).lineIds(List.of()).build();
        PurchaseDTO updated = PurchaseDTO.builder().id(5L).date(input.getDate()).lineIds(List.of()).build();
        when(purchaseService.update(5L, input)).thenReturn(updated);

        var response = purchaseController.update(5L, input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(5L);
        verify(purchaseService).update(5L, input);
    }

    @Test
    void delete_callsService() {
        doNothing().when(purchaseService).delete(7L);

        var response = purchaseController.delete(7L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(purchaseService).delete(7L);
    }
}
