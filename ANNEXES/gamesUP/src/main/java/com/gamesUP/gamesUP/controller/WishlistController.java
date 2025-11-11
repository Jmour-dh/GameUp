package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.WishlistDTO;
import com.gamesUP.gamesUP.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
@Validated
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<WishlistDTO> createWishlist(@Valid @RequestBody WishlistDTO wishlistDTO) {
        WishlistDTO created = wishlistService.createWishlist(wishlistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<List<WishlistDTO>> getAll() {
        return ResponseEntity.ok(wishlistService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<WishlistDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(wishlistService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WishlistDTO> update(@PathVariable Long id, @Valid @RequestBody WishlistDTO wishlistDTO) {
        return ResponseEntity.ok(wishlistService.update(id, wishlistDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        wishlistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
