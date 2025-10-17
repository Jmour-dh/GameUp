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
    }

    @Test
    void testDelete() {
        when(avisRepository.existsById(1L)).thenReturn(true);

        avisService.delete(1L);

        verify(avisRepository, times(1)).existsById(1L);
        verify(avisRepository, times(1)).deleteById(1L);
    }
}
