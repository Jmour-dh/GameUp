package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.entity.Category;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Publisher;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PublisherRepository;
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

class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PublisherRepository publisherRepository;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Game g = mock(Game.class);
        when(g.getId()).thenReturn(1L);
        when(g.getName()).thenReturn("Game A");

        Author author = mock(Author.class);
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Author A");
        when(g.getAuthor()).thenReturn(author);

        when(g.getGenre()).thenReturn("Action");

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);
        when(g.getCategory()).thenReturn(category);

        Publisher publisher = mock(Publisher.class);
        when(publisher.getId()).thenReturn(1L);
        when(g.getPublisher()).thenReturn(publisher);

        when(g.getNumEdition()).thenReturn(1);

        when(gameRepository.findAll()).thenReturn(singletonList(g));

        List<GameDTO> result = gameService.getAll();

        assertEquals(1, result.size());
        assertEquals("Game A", result.get(0).getName());
        verify(gameRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        Game g = mock(Game.class);
        when(g.getId()).thenReturn(1L);
        when(g.getName()).thenReturn("Game A");

        Author author = mock(Author.class);
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Author A");
        when(g.getAuthor()).thenReturn(author);

        when(g.getGenre()).thenReturn("Action");

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);
        when(g.getCategory()).thenReturn(category);

        Publisher publisher = mock(Publisher.class);
        when(publisher.getId()).thenReturn(1L);
        when(g.getPublisher()).thenReturn(publisher);

        when(g.getNumEdition()).thenReturn(1);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(g));

        GameDTO dto = gameService.getById(1L);

        assertNotNull(dto);
        assertEquals("Game A", dto.getName());
        verify(gameRepository, times(1)).findById(1L);
    }

    @Test
    void testCreate() {
        GameDTO request = new GameDTO(null, "Game A", "Author A", "Action", 1L, 1L, 1);

        Author author = mock(Author.class);
        when(author.getId()).thenReturn(1L);
        when(author.getName()).thenReturn("Author A");

        Category category = mock(Category.class);
        when(category.getId()).thenReturn(1L);

        Publisher publisher = mock(Publisher.class);
        when(publisher.getId()).thenReturn(1L);

        when(authorRepository.findByName("Author A")).thenReturn(Optional.of(author));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

        Game saved = mock(Game.class);
        when(saved.getId()).thenReturn(1L);
        when(saved.getName()).thenReturn("Game A");
        when(saved.getAuthor()).thenReturn(author);
        when(saved.getGenre()).thenReturn("Action");
        when(saved.getCategory()).thenReturn(category);
        when(saved.getPublisher()).thenReturn(publisher);
        when(saved.getNumEdition()).thenReturn(1);

        when(gameRepository.save(any(Game.class))).thenReturn(saved);

        GameDTO created = gameService.create(request);

        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Game A", created.getName());
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void testUpdate() {
        Game existing = mock(Game.class);
        when(existing.getId()).thenReturn(1L);
        when(existing.getName()).thenReturn("Old Name");

        Author oldAuthor = mock(Author.class);
        when(oldAuthor.getId()).thenReturn(1L);
        when(oldAuthor.getName()).thenReturn("Old Author");
        when(existing.getAuthor()).thenReturn(oldAuthor);

        when(existing.getGenre()).thenReturn("Old");

        Category oldCategory = mock(Category.class);
        when(oldCategory.getId()).thenReturn(1L);
        when(existing.getCategory()).thenReturn(oldCategory);

        Publisher oldPublisher = mock(Publisher.class);
        when(oldPublisher.getId()).thenReturn(1L);
        when(existing.getPublisher()).thenReturn(oldPublisher);

        when(existing.getNumEdition()).thenReturn(1);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existing));

        Author newAuthor = mock(Author.class);
        when(newAuthor.getId()).thenReturn(2L);
        when(newAuthor.getName()).thenReturn("Author A");

        Category newCategory = mock(Category.class);
        when(newCategory.getId()).thenReturn(2L);

        Publisher newPublisher = mock(Publisher.class);
        when(newPublisher.getId()).thenReturn(2L);

        when(authorRepository.findByName("Author A")).thenReturn(Optional.of(newAuthor));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(newPublisher));

        Game saved = mock(Game.class);
        when(saved.getId()).thenReturn(1L);
        when(saved.getName()).thenReturn("Updated Name");
        when(saved.getAuthor()).thenReturn(newAuthor);
        when(saved.getGenre()).thenReturn("RPG");
        when(saved.getCategory()).thenReturn(newCategory);
        when(saved.getPublisher()).thenReturn(newPublisher);
        when(saved.getNumEdition()).thenReturn(2);

        when(gameRepository.save(existing)).thenReturn(saved);

        GameDTO updateRequest = new GameDTO(null, "Updated Name", "Author A", "RPG", 2L, 2L, 2);
        GameDTO updated = gameService.update(1L, updateRequest);

        assertNotNull(updated);
        assertEquals("Updated Name", updated.getName());
        assertEquals("RPG", updated.getGenre());
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(1)).save(existing);
    }

    @Test
    void testDelete() {
        when(gameRepository.existsById(1L)).thenReturn(true);

        gameService.delete(1L);

        verify(gameRepository, times(1)).existsById(1L);
        verify(gameRepository, times(1)).deleteById(1L);
    }
}
