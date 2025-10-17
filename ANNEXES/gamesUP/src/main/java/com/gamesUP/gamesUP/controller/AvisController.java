package com.gamesUP.gamesUP.controller;

import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.service.AvisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
public class AvisController {

    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<AvisDTO> create(@Valid @RequestBody AvisDTO avisDTO) {
        AvisDTO created = avisService.create(avisDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<List<AvisDTO>> getAll() {
        return ResponseEntity.ok(avisService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<AvisDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(avisService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<AvisDTO> update(@PathVariable Long id, @Valid @RequestBody AvisDTO avisDTO) {
        AvisDTO updated = avisService.update(id, avisDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        avisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
