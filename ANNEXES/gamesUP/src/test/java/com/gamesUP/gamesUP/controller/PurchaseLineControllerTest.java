package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.service.PurchaseLineService;
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
class PurchaseLineControllerTest {

    @Mock
    private PurchaseLineService purchaseLineService;

    @InjectMocks
    private PurchaseLineController purchaseLineController;

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void getAll_returnsList() {
        PurchaseLineDTO dto = PurchaseLineDTO.builder().id(1L).userId(2).gameId(3L).price(9.99).purchaseId(4L).build();
        when(purchaseLineService.findAll()).thenReturn(List.of(dto));

        ResponseEntity<List<PurchaseLineDTO>> response = purchaseLineController.getAll();

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        verify(purchaseLineService, times(1)).findAll();
    }

    @Test
    void getById_returnsDto() {
        PurchaseLineDTO dto = PurchaseLineDTO.builder().id(2L).userId(1).gameId(5L).price(4.5).purchaseId(6L).build();
        when(purchaseLineService.findById(2L)).thenReturn(dto);

        ResponseEntity<PurchaseLineDTO> response = purchaseLineController.getById(2L);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(2L);
        verify(purchaseLineService).findById(2L);
    }

    @Test
    void getById_notFound_propagatesException() {
        when(purchaseLineService.findById(99L)).thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Not found"));

        assertThrows(ResponseStatusException.class, () -> purchaseLineController.getById(99L));
        verify(purchaseLineService).findById(99L);
    }

    @Test
    void create_returnsCreated_withLocation() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/purchase-lines");
        request.setServerName("localhost");
        request.setServerPort(8080);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        PurchaseLineDTO input = PurchaseLineDTO.builder().userId(2).gameId(3L).price(12.0).purchaseId(4L).build();
        PurchaseLineDTO created = PurchaseLineDTO.builder().id(10L).userId(2).gameId(3L).price(12.0).purchaseId(4L).build();
        when(purchaseLineService.create(any(PurchaseLineDTO.class))).thenReturn(created);

        ResponseEntity<PurchaseLineDTO> response = purchaseLineController.create(input);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
        assertThat(response.getBody().getId()).isEqualTo(10L);
        assertThat(response.getHeaders().getLocation()).isNotNull();
        verify(purchaseLineService).create(input);
    }

    @Test
    void update_returnsUpdated() {
        PurchaseLineDTO input = PurchaseLineDTO.builder().userId(7).gameId(8L).price(5.5).purchaseId(9L).build();
        PurchaseLineDTO updated = PurchaseLineDTO.builder().id(5L).userId(7).gameId(8L).price(5.5).purchaseId(9L).build();
        when(purchaseLineService.update(5L, input)).thenReturn(updated);

        ResponseEntity<PurchaseLineDTO> response = purchaseLineController.update(5L, input);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getId()).isEqualTo(5L);
        verify(purchaseLineService).update(5L, input);
    }

    @Test
    void delete_callsService() {
        doNothing().when(purchaseLineService).delete(7L);

        ResponseEntity<Void> response = purchaseLineController.delete(7L);

        assertThat(response.getStatusCodeValue()).isEqualTo(204);
        verify(purchaseLineService).delete(7L);
    }
}
