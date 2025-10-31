package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.service.PurchaseLineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/purchase-lines")
@Validated
public class PurchaseLineController {

    private final PurchaseLineService purchaseLineService;

    @Autowired
    public PurchaseLineController(PurchaseLineService purchaseLineService) {
        this.purchaseLineService = purchaseLineService;
    }

    @GetMapping
    public ResponseEntity<List<PurchaseLineDTO>> getAll() {
        return ResponseEntity.ok(purchaseLineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseLineDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(purchaseLineService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PurchaseLineDTO> create(@Valid @RequestBody PurchaseLineDTO dto) {
        PurchaseLineDTO created = purchaseLineService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PurchaseLineDTO> update(@PathVariable Long id, @Valid @RequestBody PurchaseLineDTO dto) {
        return ResponseEntity.ok(purchaseLineService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        purchaseLineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
