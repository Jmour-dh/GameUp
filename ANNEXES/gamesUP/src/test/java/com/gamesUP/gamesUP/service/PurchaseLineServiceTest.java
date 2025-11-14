package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class PurchaseLineServiceTest {

    @Autowired
    private PurchaseLineService purchaseLineService;

    @MockBean
    private PurchaseLineRepository purchaseLineRepository;

    @MockBean
    private GameRepository gameRepository;

    @MockBean
    private PurchaseRepository purchaseRepository;

    @Test
    void create_successful() {
        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(2).gameId(3L).price(7.5).purchaseId(4L).build();

        Game game = new Game(); game.setId(3L);
        Purchase purchase = new Purchase(); purchase.setId(4L);

        when(gameRepository.findById(3L)).thenReturn(Optional.of(game));
        when(purchaseRepository.findById(4L)).thenReturn(Optional.of(purchase));

        PurchaseLine saved = new PurchaseLine();
        saved.setId(100L);
        saved.setGame(game);
        saved.setPurchase(purchase);
        saved.setPrice(7.5);
        saved.setUserId(2);

        when(purchaseLineRepository.save(any(PurchaseLine.class))).thenReturn(saved);

        PurchaseLineDTO result = purchaseLineService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getGameId()).isEqualTo(3L);
        assertThat(result.getPurchaseId()).isEqualTo(4L);

        verify(gameRepository).findById(3L);
        verify(purchaseRepository).findById(4L);
        verify(purchaseLineRepository).save(any(PurchaseLine.class));
    }

    @Test
    void create_gameNotFound_throws() {
        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(2).gameId(99L).price(1.0).purchaseId(4L).build();
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.create(dto));
        verify(gameRepository).findById(99L);
        verify(purchaseRepository, never()).findById(any());
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void create_purchaseNotFound_throws() {
        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(2).gameId(3L).price(1.0).purchaseId(999L).build();
        Game game = new Game(); game.setId(3L);
        when(gameRepository.findById(3L)).thenReturn(Optional.of(game));
        when(purchaseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.create(dto));
        verify(gameRepository).findById(3L);
        verify(purchaseRepository).findById(999L);
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void findAll_returnsList() {
        PurchaseLine p1 = new PurchaseLine(); p1.setId(1L); p1.setUserId(2); p1.setPrice(3.0);
        Game g = new Game(); g.setId(5L); p1.setGame(g);
        Purchase pur = new Purchase(); pur.setId(6L); p1.setPurchase(pur);

        when(purchaseLineRepository.findAll()).thenReturn(List.of(p1));

        var list = purchaseLineService.findAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getGameId()).isEqualTo(5L);
        verify(purchaseLineRepository).findAll();
    }

    @Test
    void findById_successful() {
        PurchaseLine p = new PurchaseLine();
        p.setId(2L);
        p.setUserId(11);
        p.setPrice(9.9);
        Game g = new Game(); g.setId(7L); p.setGame(g);
        Purchase pur = new Purchase(); pur.setId(8L); p.setPurchase(pur);

        when(purchaseLineRepository.findById(2L)).thenReturn(Optional.of(p));

        var dto = purchaseLineService.findById(2L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getGameId()).isEqualTo(7L);
        assertThat(dto.getPurchaseId()).isEqualTo(8L);
        assertThat(dto.getUserId()).isEqualTo(11);
        verify(purchaseLineRepository).findById(2L);
    }

    @Test
    void findById_notFound_throws() {
        when(purchaseLineRepository.findById(500L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.findById(500L));
        verify(purchaseLineRepository).findById(500L);
    }

    @Test
    void update_changeGameAndPurchase_successful() {
        Long id = 10L;
        PurchaseLine existing = new PurchaseLine();
        existing.setId(id);
        Game oldGame = new Game(); oldGame.setId(1L);
        Purchase oldPurchase = new Purchase(); oldPurchase.setId(2L);
        existing.setGame(oldGame);
        existing.setPurchase(oldPurchase);
        existing.setPrice(2.0);
        existing.setUserId(3);

        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));

        Game newGame = new Game(); newGame.setId(8L);
        Purchase newPurchase = new Purchase(); newPurchase.setId(9L);
        when(gameRepository.findById(8L)).thenReturn(Optional.of(newGame));
        when(purchaseRepository.findById(9L)).thenReturn(Optional.of(newPurchase));

        PurchaseLine saved = new PurchaseLine();
        saved.setId(id);
        saved.setGame(newGame);
        saved.setPurchase(newPurchase);
        saved.setPrice(5.0);
        saved.setUserId(7);

        when(purchaseLineRepository.save(existing)).thenReturn(saved);

        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(7).gameId(8L).price(5.0).purchaseId(9L).build();

        PurchaseLineDTO result = purchaseLineService.update(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getGameId()).isEqualTo(8L);
        assertThat(result.getPurchaseId()).isEqualTo(9L);
        assertThat(result.getUserId()).isEqualTo(7);

        verify(purchaseLineRepository).findById(id);
        verify(gameRepository).findById(8L);
        verify(purchaseRepository).findById(9L);
        verify(purchaseLineRepository).save(existing);
    }

    @Test
    void update_gameNotFound_throws() {
        Long id = 20L;
        PurchaseLine existing = new PurchaseLine();
        existing.setId(id);
        Game oldGame = new Game(); oldGame.setId(2L);
        existing.setGame(oldGame);
        existing.setPurchase(new Purchase()); existing.getPurchase().setId(3L);

        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(gameRepository.findById(999L)).thenReturn(Optional.empty());

        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(5).gameId(999L).price(1.0).purchaseId(3L).build();

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.update(id, dto));
        verify(purchaseLineRepository).findById(id);
        verify(gameRepository).findById(999L);
        verify(purchaseRepository, never()).findById(any());
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void update_purchaseNotFound_throws() {
        Long id = 21L;
        PurchaseLine existing = new PurchaseLine();
        existing.setId(id);
        Game g = new Game(); g.setId(5L);
        existing.setGame(g);
        Purchase oldP = new Purchase(); oldP.setId(6L);
        existing.setPurchase(oldP);

        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));
        when(purchaseRepository.findById(888L)).thenReturn(Optional.empty());

        // gameId equals existing -> no game lookup
        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(9).gameId(5L).price(2.2).purchaseId(888L).build();

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.update(id, dto));
        verify(purchaseLineRepository).findById(id);
        verify(gameRepository, never()).findById(anyLong());
        verify(purchaseRepository).findById(888L);
        verify(purchaseLineRepository, never()).save(any());
    }

    @Test
    void update_gameIdNull_noLookupGame_andSaves() {
        Long id = 30L;
        PurchaseLine existing = new PurchaseLine();
        existing.setId(id);
        Game g = new Game(); g.setId(11L);
        existing.setGame(g);
        Purchase p = new Purchase(); p.setId(12L);
        existing.setPurchase(p);
        existing.setPrice(1.1);
        existing.setUserId(4);

        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));

        PurchaseLine saved = new PurchaseLine();
        saved.setId(id);
        saved.setGame(g);
        saved.setPurchase(p);
        saved.setPrice(3.3);
        saved.setUserId(10);

        when(purchaseLineRepository.save(existing)).thenReturn(saved);

        // gameId null -> should not call gameRepository.findById
        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(10).gameId(null).price(3.3).purchaseId(12L).build();

        PurchaseLineDTO result = purchaseLineService.update(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getGameId()).isEqualTo(11L);
        assertThat(result.getUserId()).isEqualTo(10);

        verify(purchaseLineRepository).findById(id);
        verify(gameRepository, never()).findById(anyLong());
        verify(purchaseLineRepository).save(existing);
    }

    @Test
    void update_purchaseIdNull_noLookupPurchase_andSaves() {
        Long id = 31L;
        PurchaseLine existing = new PurchaseLine();
        existing.setId(id);
        Game oldGame = new Game(); oldGame.setId(13L);
        existing.setGame(oldGame);
        existing.setPurchase(null);
        existing.setPrice(0.5);
        existing.setUserId(2);

        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));

        Game newGame = new Game(); newGame.setId(14L);
        when(gameRepository.findById(14L)).thenReturn(Optional.of(newGame));

        PurchaseLine saved = new PurchaseLine();
        saved.setId(id);
        saved.setGame(newGame);
        saved.setPurchase(null);
        saved.setPrice(4.4);
        saved.setUserId(6);

        when(purchaseLineRepository.save(existing)).thenReturn(saved);

        // purchaseId null -> should not call purchaseRepository.findById
        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(6).gameId(14L).price(4.4).purchaseId(null).build();

        PurchaseLineDTO result = purchaseLineService.update(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.getGameId()).isEqualTo(14L);
        assertThat(result.getPurchaseId()).isNull();
        verify(purchaseLineRepository).findById(id);
        verify(gameRepository).findById(14L);
        verify(purchaseRepository, never()).findById(anyLong());
        verify(purchaseLineRepository).save(existing);
    }

    @Test
    void update_existingGameNull_andNewGameFound_callsLookupAndSaves() {
        Long id = 40L;
        PurchaseLine existing = new PurchaseLine();
        existing.setId(id);
        existing.setGame(null);
        existing.setPurchase(null);
        existing.setUserId(1);
        existing.setPrice(0.0);

        when(purchaseLineRepository.findById(id)).thenReturn(Optional.of(existing));

        Game newGame = new Game(); newGame.setId(99L);
        when(gameRepository.findById(99L)).thenReturn(Optional.of(newGame));

        PurchaseLine saved = new PurchaseLine();
        saved.setId(id);
        saved.setGame(newGame);
        saved.setPurchase(null);
        saved.setUserId(2);
        saved.setPrice(6.6);

        when(purchaseLineRepository.save(existing)).thenReturn(saved);

        PurchaseLineDTO dto = PurchaseLineDTO.builder().userId(2).gameId(99L).price(6.6).purchaseId(null).build();

        PurchaseLineDTO result = purchaseLineService.update(id, dto);

        assertThat(result).isNotNull();
        assertThat(result.getGameId()).isEqualTo(99L);
        verify(purchaseLineRepository).findById(id);
        verify(gameRepository).findById(99L);
        verify(purchaseLineRepository).save(existing);
    }

    @Test
    void delete_notFound_throws() {
        when(purchaseLineRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> purchaseLineService.delete(777L));
        verify(purchaseLineRepository).findById(777L);
    }

    @Test
    void delete_successful_callsRepository() {
        PurchaseLine p = new PurchaseLine(); p.setId(50L);
        when(purchaseLineRepository.findById(50L)).thenReturn(Optional.of(p));
        doNothing().when(purchaseLineRepository).delete(p);

        purchaseLineService.delete(50L);

        verify(purchaseLineRepository).findById(50L);
        verify(purchaseLineRepository).delete(p);
    }
}
