package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PurchaseDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Purchase;
import com.gamesUP.gamesUP.entity.PurchaseLine;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private PurchaseLineRepository purchaseLineRepository;

    @Autowired
    private GameRepository gameRepository;

    public List<PurchaseDTO> findAll() {
        return purchaseRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PurchaseDTO findById(Long id) {
        Purchase p = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));
        return toDTO(p);
    }

    public PurchaseDTO create(PurchaseDTO dto) {
        Purchase p = fromDTO(dto);
        Purchase saved = purchaseRepository.save(p);

        // Link lines to purchase if provided (by ids)
        if (dto.getLineIds() != null) {
            dto.getLineIds().forEach(lineId -> {
                purchaseLineRepository.findById(lineId).ifPresent(line -> {
                    ensureLineHasManagedGame(line);
                    line.setPurchase(saved);
                    purchaseLineRepository.save(line);
                });
            });
        }

        return toDTO(saved);
    }

    public PurchaseDTO update(Long id, PurchaseDTO dto) {
        Purchase existing = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        existing.setDate(dto.getDate());
        existing.setPaid(dto.isPaid());
        existing.setDelivered(dto.isDelivered());
        existing.setArchived(dto.isArchived());

        Purchase saved = purchaseRepository.save(existing);

        // Detach existing lines that are not in dto.lineIds
        List<PurchaseLine> allLines = purchaseLineRepository.findAll();
        allLines.stream()
                .filter(l -> l.getPurchase() != null && Objects.equals(l.getPurchase().getId(), id))
                .forEach(l -> {
                    if (dto.getLineIds() == null || !dto.getLineIds().contains(l.getId())) {
                        l.setPurchase(null);
                        purchaseLineRepository.save(l);
                    }
                });

        // Attach new lines (ensure game managed)
        if (dto.getLineIds() != null) {
            dto.getLineIds().forEach(lineId -> {
                purchaseLineRepository.findById(lineId).ifPresent(line -> {
                    ensureLineHasManagedGame(line);
                    line.setPurchase(saved);
                    purchaseLineRepository.save(line);
                });
            });
        }

        return toDTO(saved);
    }

    public void delete(Long id) {
        Purchase p = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Purchase not found"));

        // Detach related lines
        purchaseLineRepository.findAll().stream()
                .filter(l -> l.getPurchase() != null && Objects.equals(l.getPurchase().getId(), id))
                .forEach(l -> {
                    l.setPurchase(null);
                    purchaseLineRepository.save(l);
                });

        purchaseRepository.delete(p);
    }

    private PurchaseDTO toDTO(Purchase p) {
        List<Long> lineIds = purchaseLineRepository.findAll().stream()
                .filter(l -> l.getPurchase() != null && Objects.equals(l.getPurchase().getId(), p.getId()))
                .map(PurchaseLine::getId)
                .collect(Collectors.toList());

        return PurchaseDTO.builder()
                .id(p.getId())
                .date(p.getDate())
                .paid(p.isPaid())
                .delivered(p.isDelivered())
                .archived(p.isArchived())
                .lineIds(lineIds)
                .build();
    }

    private Purchase fromDTO(PurchaseDTO dto) {
        Purchase p = new Purchase();
        p.setDate(dto.getDate());
        p.setPaid(dto.isPaid());
        p.setDelivered(dto.isDelivered());
        p.setArchived(dto.isArchived());
        return p;
    }

    /**
     * Vérifie que la ligne contient un game avec un id, puis remplace par l'entité gérée.
     * Lance une ResponseStatusException si game absent ou introuvable.
     */
    private void ensureLineHasManagedGame(PurchaseLine line) {
        if (line.getGame() == null || line.getGame().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chaque PurchaseLine doit contenir game.id");
        }
        Long gameId = line.getGame().getId();
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game introuvable id=" + gameId));
        line.setGame(game);
    }
}
