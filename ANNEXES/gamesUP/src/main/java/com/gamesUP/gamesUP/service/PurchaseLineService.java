package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.PurchaseLineDTO;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.entity.Purchase;
import com.gamesUP.gamesUP.entity.PurchaseLine;
import com.gamesUP.gamesUP.repository.GameRepository;
import com.gamesUP.gamesUP.repository.PurchaseLineRepository;
import com.gamesUP.gamesUP.repository.PurchaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PurchaseLineService {

    private final PurchaseLineRepository purchaseLineRepository;
    private final GameRepository gameRepository;
    private final PurchaseRepository purchaseRepository;

    public PurchaseLineService(PurchaseLineRepository purchaseLineRepository,
                               GameRepository gameRepository,
                               PurchaseRepository purchaseRepository) {
        this.purchaseLineRepository = purchaseLineRepository;
        this.gameRepository = gameRepository;
        this.purchaseRepository = purchaseRepository;
    }

    public PurchaseLineDTO create(PurchaseLineDTO dto) {
        Game game = gameRepository.findById(dto.getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game introuvable id=" + dto.getGameId()));
        Purchase purchase = purchaseRepository.findById(dto.getPurchaseId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase introuvable id=" + dto.getPurchaseId()));

        PurchaseLine line = new PurchaseLine();
        line.setGame(game);
        line.setPurchase(purchase);
        line.setPrice(dto.getPrice());
        line.setUserId(dto.getUserId());

        PurchaseLine saved = purchaseLineRepository.save(line);
        return toDTO(saved);
    }

    public List<PurchaseLineDTO> findAll() {
        return purchaseLineRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PurchaseLineDTO findById(Long id) {
        PurchaseLine line = purchaseLineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PurchaseLine not found"));
        return toDTO(line);
    }

    public PurchaseLineDTO update(Long id, PurchaseLineDTO dto) {
        PurchaseLine existing = purchaseLineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PurchaseLine not found"));

        if (dto.getGameId() != null && !dto.getGameId().equals(existing.getGame() != null ? existing.getGame().getId() : null)) {
            Game game = gameRepository.findById(dto.getGameId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Game introuvable id=" + dto.getGameId()));
            existing.setGame(game);
        }
        if (dto.getPurchaseId() != null && !dto.getPurchaseId().equals(existing.getPurchase() != null ? existing.getPurchase().getId() : null)) {
            Purchase purchase = purchaseRepository.findById(dto.getPurchaseId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase introuvable id=" + dto.getPurchaseId()));
            existing.setPurchase(purchase);
        }

        existing.setPrice(dto.getPrice());
        existing.setUserId(dto.getUserId());

        PurchaseLine saved = purchaseLineRepository.save(existing);
        return toDTO(saved);
    }

    public void delete(Long id) {
        PurchaseLine line = purchaseLineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PurchaseLine not found"));
        purchaseLineRepository.delete(line);
    }

    private PurchaseLineDTO toDTO(PurchaseLine line) {
        return PurchaseLineDTO.builder()
                .id(line.getId())
                .userId(line.getUserId())
                .gameId(line.getGame() != null ? line.getGame().getId() : null)
                .price(line.getPrice())
                .purchaseId(line.getPurchase() != null ? line.getPurchase().getId() : null)
                .build();
    }
}
