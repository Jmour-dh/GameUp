package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.service.PurchaseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<List<PurchaseDTO>> getAll() {
        return ResponseEntity.ok(purchaseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseDTO> create(@RequestBody @Valid PurchaseDTO dto) {
        PurchaseDTO created = purchaseService.create(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseDTO> update(@PathVariable Long id, @RequestBody @Valid PurchaseDTO dto) {
        return ResponseEntity.ok(purchaseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}