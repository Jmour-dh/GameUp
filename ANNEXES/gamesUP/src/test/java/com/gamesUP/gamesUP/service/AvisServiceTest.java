package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.entity.Avis;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AvisRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvisServiceTest {

    @Mock
    private AvisRepository avisRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private AvisService avisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Avis a = mock(Avis.class);
        when(a.getId()).thenReturn(1L);
        when(a.getComment()).thenReturn("Super");
        when(a.getNote()).thenReturn(5);

        Game g = mock(Game.class);
        when(g.getId()).thenReturn(1L);
        when(a.getGame()).thenReturn(g);

        when(avisRepository.findAll()).thenReturn(singletonList(a));

        List<AvisDTO> result = avisService.findAll();

        assertEquals(1, result.size());
        assertEquals("Super", result.get(0).getComment());
        assertEquals(5, result.get(0).getNote());
        verify(avisRepository, times(1)).findAll();
    }

    @Test
    void testFindAll_empty_returnsEmptyList() {
        when(avisRepository.findAll()).thenReturn(List.of());

        List<AvisDTO> result = avisService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(avisRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Avis a = mock(Avis.class);
        when(a.getId()).thenReturn(1L);
        when(a.getComment()).thenReturn("Bien");
        when(a.getNote()).thenReturn(4);

        Game g = mock(Game.class);
        when(g.getId()).thenReturn(1L);
        when(a.getGame()).thenReturn(g);

        when(avisRepository.findById(1L)).thenReturn(Optional.of(a));

        AvisDTO dto = avisService.findById(1L);

        assertNotNull(dto);
        assertEquals("Bien", dto.getComment());
        assertEquals(4, dto.getNote());
        verify(avisRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_notFound_throws() {
        when(avisRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> avisService.findById(999L));
        verify(avisRepository, times(1)).findById(999L);
    }

    @Test
    void testCreate() {
        AvisDTO request = new AvisDTO(null, "Nice", 5, 1L);

        Game g = mock(Game.class);
        when(g.getId()).thenReturn(1L);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(g));

        Avis saved = mock(Avis.class);
        when(saved.getId()).thenReturn(1L);
        when(saved.getComment()).thenReturn("Nice");
        when(saved.getNote()).thenReturn(5);
        when(saved.getGame()).thenReturn(g);

        when(avisRepository.save(any(Avis.class))).thenReturn(saved);

        AvisDTO created = avisService.create(request);

        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Nice", created.getComment());
        verify(avisRepository, times(1)).save(any(Avis.class));
    }

    @Test
    void testCreate_gameNotFound_throws() {
        AvisDTO request = new AvisDTO(null, "Nice", 5, 42L);
        when(gameRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> avisService.create(request));
        verify(gameRepository, times(1)).findById(42L);
        verify(avisRepository, never()).save(any());
    }

    @Test
    void testUpdate() {
        Avis existing = mock(Avis.class);
        when(existing.getId()).thenReturn(1L);
        when(existing.getComment()).thenReturn("Old");
        when(existing.getNote()).thenReturn(3);

        Game oldGame = mock(Game.class);
        when(oldGame.getId()).thenReturn(1L);
        when(existing.getGame()).thenReturn(oldGame);

        when(avisRepository.findById(1L)).thenReturn(Optional.of(existing));

        Game newGame = mock(Game.class);
        when(newGame.getId()).thenReturn(2L);
        when(gameRepository.findById(2L)).thenReturn(Optional.of(newGame));

        Avis saved = mock(Avis.class);
        when(saved.getId()).thenReturn(1L);
        when(saved.getComment()).thenReturn("Updated");
        when(saved.getNote()).thenReturn(4);
        when(saved.getGame()).thenReturn(newGame);

        when(avisRepository.save(existing)).thenReturn(saved);

        AvisDTO updateRequest = new AvisDTO(null, "Updated", 4, 2L);
        AvisDTO updated = avisService.update(1L, updateRequest);

        assertNotNull(updated);
        assertEquals("Updated", updated.getComment());
        assertEquals(4, updated.getNote());
        verify(avisRepository, times(1)).findById(1L);
        verify(avisRepository, times(1)).save(existing);
        verify(gameRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdate_avisNotFound_throws() {
        when(avisRepository.findById(7L)).thenReturn(Optional.empty());
        AvisDTO req = new AvisDTO(null, "X", 3, 1L);

        assertThrows(RuntimeException.class, () -> avisService.update(7L, req));
        verify(avisRepository, times(1)).findById(7L);
        verify(gameRepository, never()).findById(anyLong());
    }

    @Test
    void testUpdate_gameNotFound_throws() {
        Avis existing = mock(Avis.class);
        when(existing.getId()).thenReturn(8L);
        Game oldGame = mock(Game.class);
        when(oldGame.getId()).thenReturn(1L);
        when(existing.getGame()).thenReturn(oldGame);

        when(avisRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        AvisDTO req = new AvisDTO(null, "Y", 2, 99L);

        assertThrows(RuntimeException.class, () -> avisService.update(8L, req));
        verify(avisRepository, times(1)).findById(8L);
        verify(gameRepository, times(1)).findById(99L);
        verify(avisRepository, never()).save(any());
    }

    @Test
    void testUpdate_noGameChange_doesNotLookupGame() {
        Avis existing = mock(Avis.class);
        when(existing.getId()).thenReturn(15L);

        Game currentGame = mock(Game.class);
        when(currentGame.getId()).thenReturn(2L);
        when(existing.getGame()).thenReturn(currentGame);

        when(avisRepository.findById(15L)).thenReturn(Optional.of(existing));

        // update request uses same game id as current -> no gameRepository lookup
        Avis saved = mock(Avis.class);
        when(saved.getId()).thenReturn(15L);
        when(saved.getComment()).thenReturn("SameGame");
        when(saved.getNote()).thenReturn(5);
        when(saved.getGame()).thenReturn(currentGame);

        when(avisRepository.save(existing)).thenReturn(saved);

        AvisDTO req = new AvisDTO(null, "SameGame", 5, 2L);
        AvisDTO result = avisService.update(15L, req);

        assertNotNull(result);
        assertEquals("SameGame", result.getComment());
        verify(avisRepository, times(1)).findById(15L);
        verify(gameRepository, never()).findById(anyLong());
        verify(avisRepository, times(1)).save(existing);
    }

    @Test
    void testUpdate_gameIdNull_doesNotLookupGameAndSaves() {
        Avis existing = mock(Avis.class);
        when(existing.getId()).thenReturn(21L);
        when(existing.getComment()).thenReturn("Before");
        when(existing.getNote()).thenReturn(2);

        Game currentGame = mock(Game.class);
        when(currentGame.getId()).thenReturn(3L);
        when(existing.getGame()).thenReturn(currentGame);

        when(avisRepository.findById(21L)).thenReturn(Optional.of(existing));

        Avis saved = mock(Avis.class);
        when(saved.getId()).thenReturn(21L);
        when(saved.getComment()).thenReturn("After");
        when(saved.getNote()).thenReturn(4);
        when(saved.getGame()).thenReturn(currentGame);

        when(avisRepository.save(existing)).thenReturn(saved);

        // gameId is null -> service should not call gameRepository.findById
        AvisDTO req = new AvisDTO(null, "After", 4, null);
        AvisDTO result = avisService.update(21L, req);

        assertNotNull(result);
        assertEquals("After", result.getComment());
        verify(avisRepository, times(1)).findById(21L);
        verify(gameRepository, never()).findById(anyLong());
        verify(avisRepository, times(1)).save(existing);
    }

    @Test
    void testUpdate_existingGameNull_andNewGameFound_callsLookupAndSaves() {
        Avis existing = mock(Avis.class);
        when(existing.getId()).thenReturn(30L);
        when(existing.getComment()).thenReturn("Start");
        when(existing.getNote()).thenReturn(1);

        // existing.getGame() returns null
        when(existing.getGame()).thenReturn(null);

        when(avisRepository.findById(30L)).thenReturn(Optional.of(existing));

        Game newGame = mock(Game.class);
        when(newGame.getId()).thenReturn(9L);
        when(gameRepository.findById(9L)).thenReturn(Optional.of(newGame));

        Avis saved = mock(Avis.class);
        when(saved.getId()).thenReturn(30L);
        when(saved.getComment()).thenReturn("WithGame");
        when(saved.getNote()).thenReturn(5);
        when(saved.getGame()).thenReturn(newGame);

        when(avisRepository.save(existing)).thenReturn(saved);

        AvisDTO req = new AvisDTO(null, "WithGame", 5, 9L);
        AvisDTO result = avisService.update(30L, req);

        assertNotNull(result);
        assertEquals("WithGame", result.getComment());
        verify(avisRepository, times(1)).findById(30L);
        verify(gameRepository, times(1)).findById(9L);
        verify(avisRepository, times(1)).save(existing);
    }

    @Test
    void testDelete() {
        when(avisRepository.existsById(1L)).thenReturn(true);

        avisService.delete(1L);

        verify(avisRepository, times(1)).existsById(1L);
        verify(avisRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDelete_notFound_throws() {
        when(avisRepository.existsById(123L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> avisService.delete(123L));
        verify(avisRepository, times(1)).existsById(123L);
        verify(avisRepository, never()).deleteById(anyLong());
    }
}
