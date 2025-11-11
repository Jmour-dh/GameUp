package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PublisherDTO;
import com.gamesUP.gamesUP.entity.Publisher;
import com.gamesUP.gamesUP.exception.ResourceNotFoundException;
import com.gamesUP.gamesUP.repository.PublisherRepository;
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
class PublisherServiceTest {

    @Autowired
    private PublisherService publisherService;

    @MockBean
    private PublisherRepository publisherRepository;

    @Test
    void create_successful() {
        PublisherDTO dto = PublisherDTO.builder().name("PubA").build();

        Publisher saved = new Publisher();
        saved.setId(100L);
        saved.setName("PubA");
        when(publisherRepository.save(any(Publisher.class))).thenReturn(saved);

        PublisherDTO result = publisherService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getName()).isEqualTo("PubA");
        verify(publisherRepository).save(any(Publisher.class));
    }

    @Test
    void getById_successful() {
        Publisher p = new Publisher();
        p.setId(2L);
        p.setName("Pub2");
        when(publisherRepository.findById(2L)).thenReturn(Optional.of(p));

        PublisherDTO dto = publisherService.getById(2L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getName()).isEqualTo("Pub2");
        verify(publisherRepository).findById(2L);
    }

    @Test
    void getById_notFound_throws() {
        when(publisherRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> publisherService.getById(99L));
        verify(publisherRepository).findById(99L);
    }

    @Test
    void getAll_returnsList() {
        Publisher p1 = new Publisher(); p1.setId(1L); p1.setName("A");
        Publisher p2 = new Publisher(); p2.setId(2L); p2.setName("B");
        when(publisherRepository.findAll()).thenReturn(List.of(p1, p2));

        List<PublisherDTO> list = publisherService.getAll();

        assertThat(list).hasSize(2);
        verify(publisherRepository).findAll();
    }

    @Test
    void update_successful() {
        Publisher existing = new Publisher();
        existing.setId(10L);
        existing.setName("OldName");
        when(publisherRepository.findById(10L)).thenReturn(Optional.of(existing));

        Publisher saved = new Publisher();
        saved.setId(10L);
        saved.setName("NewName");
        when(publisherRepository.save(existing)).thenReturn(saved);

        PublisherDTO dto = PublisherDTO.builder().name("NewName").build();

        PublisherDTO result = publisherService.update(10L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("NewName");

        verify(publisherRepository).findById(10L);
        verify(publisherRepository).save(existing);
    }

    @Test
    void delete_notFound_throws() {
        when(publisherRepository.existsById(777L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> publisherService.delete(777L));
        verify(publisherRepository).existsById(777L);
    }

    @Test
    void delete_successful_callsRepository() {
        when(publisherRepository.existsById(5L)).thenReturn(true);
        doNothing().when(publisherRepository).deleteById(5L);

        publisherService.delete(5L);

        verify(publisherRepository).existsById(5L);
        verify(publisherRepository).deleteById(5L);
    }
}
