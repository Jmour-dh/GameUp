package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.entity.Wishlist;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.WishlistRepository;
import com.gamesUP.gamesUP.repository.UserRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public WishlistDTO createWishlist(WishlistDTO wishlistDTO) {
        User user = userRepository.findById(wishlistDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Game game = gameRepository.findById(wishlistDTO.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setGame(game);

        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToDTO(saved);
    }

    public List<WishlistDTO> findAll() {
        return wishlistRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public WishlistDTO findById(Long id) {
        Wishlist w = wishlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found"));
        return mapToDTO(w);
    }

    public WishlistDTO update(Long id, WishlistDTO wishlistDTO) {
        Wishlist existing = wishlistRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found"));

        User user = userRepository.findById(wishlistDTO.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Game game = gameRepository.findById(wishlistDTO.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        existing.setUser(user);
        existing.setGame(game);

        Wishlist saved = wishlistRepository.save(existing);
        return mapToDTO(saved);
    }

    public void delete(Long id) {
        if (!wishlistRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wishlist not found");
        }
        wishlistRepository.deleteById(id);
    }

    private WishlistDTO mapToDTO(Wishlist w) {
        WishlistDTO dto = new WishlistDTO();
        dto.setId(w.getId());
        dto.setUserId(w.getUser().getId());
        dto.setGameId(w.getGame().getId());
        return dto;
    }
}
