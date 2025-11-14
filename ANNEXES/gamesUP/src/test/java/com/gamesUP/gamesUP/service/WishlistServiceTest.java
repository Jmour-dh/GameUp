package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.entity.Wishlist;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class WishlistServiceTest {

    @Autowired
    private WishlistService wishlistService;

    @MockBean
    private WishlistRepository wishlistRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    @Test
    void createWishlist_successful() {
        User user = new User();
        user.setId(1L);
        Game game = new Game();
        game.setId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(2L)).thenReturn(Optional.of(game));

        Wishlist saved = new Wishlist();
        saved.setId(100L);
        saved.setUser(user);
        saved.setGame(game);

        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(saved);

        WishlistDTO dto = new WishlistDTO();
        dto.setUserId(1L);
        dto.setGameId(2L);

        WishlistDTO result = wishlistService.createWishlist(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getGameId()).isEqualTo(2L);

        verify(userRepository).findById(1L);
        verify(gameRepository).findById(2L);
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void createWishlist_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        WishlistDTO dto = new WishlistDTO();
        dto.setUserId(99L);
        dto.setGameId(2L);

        assertThrows(ResponseStatusException.class, () -> wishlistService.createWishlist(dto));
        verify(userRepository).findById(99L);
        verify(gameRepository, never()).findById(any());
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void createWishlist_gameNotFound_throws() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(gameRepository.findById(55L)).thenReturn(Optional.empty());

        WishlistDTO dto = new WishlistDTO();
        dto.setUserId(1L);
        dto.setGameId(55L);

        assertThrows(ResponseStatusException.class, () -> wishlistService.createWishlist(dto));
        verify(userRepository).findById(1L);
        verify(gameRepository).findById(55L);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void findAll_returnsList() {
        User user = new User(); user.setId(1L);
        Game game = new Game(); game.setId(2L);
        Wishlist w = new Wishlist();
        w.setId(5L);
        w.setUser(user);
        w.setGame(game);

        when(wishlistRepository.findAll()).thenReturn(List.of(w));

        List<WishlistDTO> result = wishlistService.findAll();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(5L);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        assertThat(result.get(0).getGameId()).isEqualTo(2L);

        verify(wishlistRepository).findAll();
    }

    @Test
    void findById_successful() {
        User user = new User(); user.setId(7L);
        Game game = new Game(); game.setId(8L);
        Wishlist w = new Wishlist();
        w.setId(20L);
        w.setUser(user);
        w.setGame(game);

        when(wishlistRepository.findById(20L)).thenReturn(Optional.of(w));

        WishlistDTO dto = wishlistService.findById(20L);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(20L);
        assertThat(dto.getUserId()).isEqualTo(7L);
        assertThat(dto.getGameId()).isEqualTo(8L);

        verify(wishlistRepository).findById(20L);
    }

    @Test
    void findById_notFound_throws() {
        when(wishlistRepository.findById(500L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> wishlistService.findById(500L));
        verify(wishlistRepository).findById(500L);
    }

    @Test
    void delete_successful() {
        when(wishlistRepository.existsById(50L)).thenReturn(true);

        wishlistService.delete(50L);

        verify(wishlistRepository).existsById(50L);
        verify(wishlistRepository).deleteById(50L);
    }

    @Test
    void delete_notFound_throws() {
        when(wishlistRepository.existsById(777L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> wishlistService.delete(777L));
        verify(wishlistRepository).existsById(777L);
    }

    @Test
    void update_successful() {
        Wishlist existing = new Wishlist();
        existing.setId(10L);
        User oldUser = new User(); oldUser.setId(1L);
        Game oldGame = new Game(); oldGame.setId(2L);
        existing.setUser(oldUser);
        existing.setGame(oldGame);

        when(wishlistRepository.findById(10L)).thenReturn(Optional.of(existing));

        User newUser = new User(); newUser.setId(3L);
        Game newGame = new Game(); newGame.setId(4L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(newUser));
        when(gameRepository.findById(4L)).thenReturn(Optional.of(newGame));

        Wishlist updated = new Wishlist();
        updated.setId(10L);
        updated.setUser(newUser);
        updated.setGame(newGame);
        when(wishlistRepository.save(existing)).thenReturn(updated);

        WishlistDTO dto = new WishlistDTO();
        dto.setUserId(3L);
        dto.setGameId(4L);

        WishlistDTO result = wishlistService.update(10L, dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getUserId()).isEqualTo(3L);
        assertThat(result.getGameId()).isEqualTo(4L);

        verify(wishlistRepository).findById(10L);
        verify(userRepository).findById(3L);
        verify(gameRepository).findById(4L);
        verify(wishlistRepository).save(existing);
    }

    @Test
    void update_userNotFound_throws() {
        Wishlist existing = new Wishlist();
        existing.setId(11L);
        User oldUser = new User(); oldUser.setId(1L);
        Game oldGame = new Game(); oldGame.setId(2L);
        existing.setUser(oldUser);
        existing.setGame(oldGame);

        when(wishlistRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        WishlistDTO dto = new WishlistDTO();
        dto.setUserId(999L);
        dto.setGameId(2L);

        assertThrows(ResponseStatusException.class, () -> wishlistService.update(11L, dto));
        verify(wishlistRepository).findById(11L);
        verify(userRepository).findById(999L);
        verify(gameRepository, never()).findById(any());
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void update_gameNotFound_throws() {
        Wishlist existing = new Wishlist();
        existing.setId(12L);
        User oldUser = new User(); oldUser.setId(1L);
        Game oldGame = new Game(); oldGame.setId(2L);
        existing.setUser(oldUser);
        existing.setGame(oldGame);

        when(wishlistRepository.findById(12L)).thenReturn(Optional.of(existing));
        User newUser = new User(); newUser.setId(3L);
        when(userRepository.findById(3L)).thenReturn(Optional.of(newUser));
        when(gameRepository.findById(444L)).thenReturn(Optional.empty());

        WishlistDTO dto = new WishlistDTO();
        dto.setUserId(3L);
        dto.setGameId(444L);

        assertThrows(ResponseStatusException.class, () -> wishlistService.update(12L, dto));
        verify(wishlistRepository).findById(12L);
        verify(userRepository).findById(3L);
        verify(gameRepository).findById(444L);
        verify(wishlistRepository, never()).save(any());
    }
}
