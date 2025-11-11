package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Purchase;
import com.gamesUP.gamesUP.entity.PurchaseLine;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class PurchaseServiceTest {

    @Autowired
    private PurchaseService purchaseService;

    @MockBean
    private PurchaseRepository purchaseRepository;

    @MockBean
    private PurchaseLineRepository purchaseLineRepository;

    @MockBean
    private GameRepository gameRepository;

    @Test
    void create_withLines_successful() {
        Date now = new Date();
        PurchaseDTO dto = PurchaseDTO.builder().date(now).lineIds(List.of(1L)).build();

        Purchase saved = new Purchase();
        saved.setId(100L);
        saved.setDate(now);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(saved);

        PurchaseLine lineBefore = new PurchaseLine();
        lineBefore.setId(1L);
        Game refGame = new Game(); refGame.setId(2L);
        lineBefore.setGame(refGame);

        Game managedGame = new Game(); managedGame.setId(2L);
        when(purchaseLineRepository.findById(1L)).thenReturn(Optional.of(lineBefore));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(managedGame));

        // after linking, toDTO will call findAll() — simulate returned line linked to saved purchase
        PurchaseLine lineWithPurchase = new PurchaseLine();
        lineWithPurchase.setId(1L);
        lineWithPurchase.setPurchase(saved);
        when(purchaseLineRepository.findAll()).thenReturn(List.of(lineWithPurchase));

        PurchaseDTO result = purchaseService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getLineIds()).containsExactly(1L);

        verify(purchaseLineRepository).findById(1L);
        verify(gameRepository).findById(2L);
        verify(purchaseLineRepository).save(any(PurchaseLine.class));
    }

    @Test
    void create_lineMissingGame_throwsBadRequest() {
        PurchaseDTO dto = PurchaseDTO.builder().date(new Date()).lineIds(List.of(1L)).build();

        Purchase saved = new Purchase();
        saved.setId(200L);
        when(purchaseRepository.save(any(Purchase.class))).thenReturn(saved);

        PurchaseLine lineBefore = new PurchaseLine();
        lineBefore.setId(1L);
        lineBefore.setGame(null); // missing game -> should trigger BAD_REQUEST
        when(purchaseLineRepository.findById(1L)).thenReturn(Optional.of(lineBefore));

        assertThrows(ResponseStatusException.class, () -> purchaseService.create(dto));
        verify(purchaseLineRepository).findById(1L);
        verify(gameRepository, never()).findById(any());
        verify(purchaseLineRepository, never()).save(argThat(l -> l.getPurchase() != null));
    }

    @Test
    void update_detachAndAttachLines_successful() {
        Date now = new Date();
        Long purchaseId = 10L;
        Purchase existing = new Purchase();
        existing.setId(purchaseId);
        existing.setDate(now);
        when(purchaseRepository.findById(purchaseId)).thenReturn(Optional.of(existing));

        // existing line attached to purchase -> should be detached (id=2)
        PurchaseLine attachedLine = new PurchaseLine();
        attachedLine.setId(2L);
        attachedLine.setPurchase(existing);

        // new line to attach id=3
        PurchaseLine newLine = new PurchaseLine();
        newLine.setId(3L);
        Game refGame = new Game(); refGame.setId(5L);
        newLine.setGame(refGame);

        when(purchaseLineRepository.findAll()).thenReturn(List.of(attachedLine));
        when(purchaseLineRepository.findById(3L)).thenReturn(Optional.of(newLine));
        Game managed = new Game(); managed.setId(5L);
        when(gameRepository.findById(5L)).thenReturn(Optional.of(managed));

        Purchase saved = new Purchase();
        saved.setId(purchaseId);
        when(purchaseRepository.save(existing)).thenReturn(saved);

        // After update, toDTO should read lines attached to saved purchase — simulate only newLine attached
        PurchaseLine newLineAttached = new PurchaseLine();
        newLineAttached.setId(3L);
        newLineAttached.setPurchase(saved);
        when(purchaseLineRepository.findAll()).thenReturn(List.of(newLineAttached));

        PurchaseDTO dto = PurchaseDTO.builder().date(now).lineIds(List.of(3L)).build();
        PurchaseDTO result = purchaseService.update(purchaseId, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(purchaseId);
        assertThat(result.getLineIds()).containsExactly(3L);

        verify(purchaseLineRepository, atLeastOnce()).findAll();
        verify(purchaseLineRepository).findById(3L);
        verify(game_repository_safe(gameRepository)).findById(5L); // helper to avoid static import clash
        verify(purchaseLineRepository, atLeast(1)).save(any(PurchaseLine.class));
    }

    // Petit wrapper pour verifier l'appel sur gameRepository sans coller de static import problématique dans le code généré.
    private GameRepository game_repository_safe(GameRepository repo) {
        return repo;
    }

    @Test
    void delete_notFound_throws() {
        when(purchaseRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> purchaseService.delete(777L));
        verify(purchaseRepository).findById(777L);
    }

    @Test
    void delete_successful_detachesLinesAndDeletes() {
        Purchase p = new Purchase();
        p.setId(50L);
        when(purchaseRepository.findById(50L)).thenReturn(Optional.of(p));

        PurchaseLine l1 = new PurchaseLine(); l1.setId(11L); l1.setPurchase(p);
        when(purchaseLineRepository.findAll()).thenReturn(List.of(l1));

        doNothing().when(purchaseRepository).delete(p);
        purchaseService.delete(50L);

        verify(purchaseLineRepository).findAll();
        verify(purchaseLineRepository).save(argThat(l -> l.getPurchase() == null));
        verify(purchaseRepository).delete(p);
    }
}
