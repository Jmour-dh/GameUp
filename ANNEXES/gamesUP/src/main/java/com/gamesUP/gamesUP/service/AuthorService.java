package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.AuthorDTO;
import com.gamesUP.gamesUP.entity.Author;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AuthorRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final GameRepository gameRepository;

    public AuthorService(AuthorRepository authorRepository, GameRepository gameRepository) {
        this.authorRepository = authorRepository;
        this.gameRepository = gameRepository;
    }

    public AuthorDTO create(AuthorDTO dto) {
        authorRepository.findByName(dto.getName()).ifPresent(a -> {
            throw new RuntimeException("Author already exists with name: " + dto.getName());
        });

        Author author = new Author();
        author.setName(dto.getName());

        List<Game> games = dto.getGameIds() == null ? Collections.emptyList()
                : gameRepository.findAllById(dto.getGameIds());
        author.setGames(games);

        Author saved = authorRepository.save(author);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public AuthorDTO getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));
        return toDto(author);
    }

    @Transactional(readOnly = true)
    public List<AuthorDTO> getAll() {
        return authorRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public AuthorDTO update(Long id, AuthorDTO dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        if (!author.getName().equals(dto.getName())) {
            authorRepository.findByName(dto.getName()).ifPresent(a -> {
                throw new RuntimeException("Author already exists with name: " + dto.getName());
            });
        }

        author.setName(dto.getName());

        List<Game> games = dto.getGameIds() == null ? Collections.emptyList()
                : gameRepository.findAllById(dto.getGameIds());
        author.setGames(games);

        Author saved = authorRepository.save(author);
        return toDto(saved);
    }

    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        authorRepository.deleteById(id);
    }

    private AuthorDTO toDto(Author author) {
        List<Long> gameIds = author.getGames() == null ? Collections.emptyList()
                : author.getGames().stream().map(Game::getId).collect(Collectors.toList());

        return AuthorDTO.builder()
                .id(author.getId())
                .name(author.getName())
                .gameIds(gameIds)
                .build();
    }
}
