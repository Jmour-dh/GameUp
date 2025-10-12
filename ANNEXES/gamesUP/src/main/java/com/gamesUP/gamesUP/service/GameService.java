package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.GameDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.entity.Category;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Publisher;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.CategoryRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public GameService(GameRepository gameRepository,
                       AuthorRepository authorRepository,
                       CategoryRepository categoryRepository,
                       PublisherRepository publisherRepository) {
        this.gameRepository = gameRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.publisherRepository = publisherRepository;
    }

    public GameDTO create(GameDTO dto) {
        Game game = toEntity(dto);
        Game saved = gameRepository.save(game);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public GameDTO getById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id " + id));
        return toDto(game);
    }

    @Transactional(readOnly = true)
    public List<GameDTO> getAll() {
        return gameRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public GameDTO update(Long id, GameDTO dto) {
        Game existing = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id " + id));

        existing.setName(dto.getName());
        existing.setGenre(dto.getGenre());
        existing.setNumEdition(dto.getNumEdition());

        Author author = authorRepository.findByName(dto.getAuthor())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + dto.getAuthor()));
        existing.setAuthor(author);

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + dto.getCategoryId()));
        existing.setCategory(category);

        Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id " + dto.getPublisherId()));
        existing.setPublisher(publisher);

        Game updated = gameRepository.save(existing);
        return toDto(updated);
    }

    public void delete(Long id) {
        if (!gameRepository.existsById(id)) {
            throw new ResourceNotFoundException("Game not found with id " + id);
        }
        gameRepository.deleteById(id);
    }

    private GameDTO toDto(Game g) {
        return GameDTO.builder()
                .id(g.getId())
                .name(g.getName())
                .author(g.getAuthor() != null ? g.getAuthor().getName() : null)
                .genre(g.getGenre())
                .categoryId(g.getCategory() != null ? g.getCategory().getId() : null)
                .publisherId(g.getPublisher() != null ? g.getPublisher().getId() : null)
                .numEdition(g.getNumEdition())
                .build();
    }

    private Game toEntity(GameDTO dto) {
        Author author = authorRepository.findByName(dto.getAuthor())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + dto.getAuthor()));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id " + dto.getCategoryId()));
        Publisher publisher = publisherRepository.findById(dto.getPublisherId())
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id " + dto.getPublisherId()));

        return Game.builder()
                .name(dto.getName())
                .author(author)
                .genre(dto.getGenre())
                .category(category)
                .publisher(publisher)
                .numEdition(dto.getNumEdition())
                .build();
    }
}
