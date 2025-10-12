package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.entity.Publisher;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    public PublisherDTO create(PublisherDTO dto) {
        Publisher entity = toEntity(dto);
        Publisher saved = publisherRepository.save(entity);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public PublisherDTO getById(Long id) {
        Publisher p = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id " + id));
        return toDto(p);
    }

    @Transactional(readOnly = true)
    public List<PublisherDTO> getAll() {
        return publisherRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PublisherDTO update(Long id, PublisherDTO dto) {
        Publisher existing = publisherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Publisher not found with id " + id));
        existing.setName(dto.getName());
        Publisher updated = publisherRepository.save(existing);
        return toDto(updated);
    }

    public void delete(Long id) {
        if (!publisherRepository.existsById(id)) {
            throw new ResourceNotFoundException("Publisher not found with id " + id);
        }
        publisherRepository.deleteById(id);
    }

    // mapping helpers
    private PublisherDTO toDto(Publisher p) {
        return PublisherDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .build();
    }

    private Publisher toEntity(PublisherDTO dto) {
        Publisher p = new Publisher();
        p.setName(dto.getName());
        return p;
    }
}
