package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthorServiceTest {

    @Autowired
    private AuthorService authorService;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private GameRepository gameRepository;

    @Test
    void create_successful() {
        AuthorDTO dto = AuthorDTO.builder().name("New Author").gameIds(List.of(2L)).build();

        when(authorRepository.findByName("New Author")).thenReturn(Optional.empty());

        Game g = new Game(); g.setId(2L);
        when(gameRepository.findAllById(List.of(2L))).thenReturn(List.of(g));

        Author saved = new Author();
        saved.setId(100L);
        saved.setName("New Author");
        saved.setGames(List.of(g));
        when(authorRepository.save(any(Author.class))).thenReturn(saved);

        AuthorDTO result = authorService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("New Author");
        assertThat(result.getGameIds()).containsExactly(2L);

        verify(authorRepository).findByName("New Author");
        verify(gameRepository).findAllById(List.of(2L));
        verify(authorRepository).save(any(Author.class));
    }

    @Test
    void create_duplicateName_throws() {
        AuthorDTO dto = AuthorDTO.builder().name("Exists").gameIds(List.of()).build();
        Author existing = new Author(); existing.setId(1L); existing.setName("Exists");
        when(authorRepository.findByName("Exists")).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> authorService.create(dto));
        verify(authorRepository).findByName("Exists");
        verify(authorRepository, never()).save(any());
    }

    @Test
    void getById_successful() {
        Author a = new Author(); a.setId(2L); a.setName("Aut");
        when(authorRepository.findById(2L)).thenReturn(Optional.of(a));

        AuthorDTO dto = authorService.getById(2L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Aut");
        verify(authorRepository).findById(2L);
    }

    @Test
    void getById_notFound_throws() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authorService.getById(99L));
        verify(authorRepository).findById(99L);
    }

    @Test
    void update_successful() {
        Author existing = new Author();
        existing.setId(10L);
        existing.setName("Old");
        when(authorRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(authorRepository.findByName("New")).thenReturn(Optional.empty());

        Game g = new Game(); g.setId(3L);
        when(gameRepository.findAllById(List.of(3L))).thenReturn(List.of(g));

        Author saved = new Author();
        saved.setId(10L);
        saved.setName("New");
        saved.setGames(List.of(g));
        when(authorRepository.save(existing)).thenReturn(saved);

        AuthorDTO dto = AuthorDTO.builder().name("New").gameIds(List.of(3L)).build();

        AuthorDTO result = authorService.update(10L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getGameIds()).containsExactly(3L);

        verify(authorRepository).findById(10L);
        verify(authorRepository).findByName("New");
        verify(gameRepository).findAllById(List.of(3L));
        verify(authorRepository).save(existing);
    }

    @Test
    void delete_notFound_throws() {
        when(authorRepository.existsById(777L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authorService.delete(777L));
        verify(authorRepository).existsById(777L);
    }

    @Test
    void delete_successful_callsRepository() {
        when(authorRepository.existsById(5L)).thenReturn(true);
        doNothing().when(authorRepository).deleteById(5L);

        authorService.delete(5L);

        verify(authorRepository).existsById(5L);
        verify(authorRepository).deleteById(5L);
    }
}