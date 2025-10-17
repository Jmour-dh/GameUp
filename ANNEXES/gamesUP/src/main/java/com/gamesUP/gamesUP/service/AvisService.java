package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.AvisDTO;
import com.gamesUP.gamesUP.entity.Avis;
import com.gamesUP.gamesUP.entity.Game;
import com.gamesUP.gamesUP.repository.AvisRepository;
import com.gamesUP.gamesUP.repository.GameRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AvisService {

    private final AvisRepository avisRepository;
    private final GameRepository gameRepository;

    public AvisService(AvisRepository avisRepository, GameRepository gameRepository) {
        this.avisRepository = avisRepository;
        this.gameRepository = gameRepository;
    }

    public AvisDTO create(AvisDTO avisDTO) {
        Game game = gameRepository.findById(avisDTO.getGameId())
                .orElseThrow(() -> new RuntimeException("Jeu non trouvé avec l'ID: " + avisDTO.getGameId()));

        Avis avis = Avis.builder()
                .comment(avisDTO.getComment())
                .note(avisDTO.getNote())
                .game(game)
                .build();

        Avis savedAvis = avisRepository.save(avis);
        return convertToDTO(savedAvis);
    }

    public AvisDTO findById(Long id) {
        Avis avis = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avis non trouvé avec l'ID: " + id));
        return convertToDTO(avis);
    }

    public List<AvisDTO> findAll() {
        return avisRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public AvisDTO update(Long id, AvisDTO avisDTO) {
        Avis existing = avisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with ID:" + id));

        existing.setComment(avisDTO.getComment());
        existing.setNote(avisDTO.getNote());

        if (avisDTO.getGameId() != null) {
            Long currentGameId = existing.getGame() != null ? existing.getGame().getId() : null;
            if (!avisDTO.getGameId().equals(currentGameId)) {
                Game game = gameRepository.findById(avisDTO.getGameId())
                        .orElseThrow(() -> new RuntimeException("Game not found with ID: " + avisDTO.getGameId()));
                existing.setGame(game);
            }
        }

        Avis saved = avisRepository.save(existing);
        return convertToDTO(saved);
    }

    public void delete(Long id) {
        if (!avisRepository.existsById(id)) {
            throw new RuntimeException("Avis non trouvé avec l'ID: " + id);
        }
        avisRepository.deleteById(id);
    }

    private AvisDTO convertToDTO(Avis avis) {
        return AvisDTO.builder()
                .id(avis.getId())
                .comment(avis.getComment())
                .note(avis.getNote())
                .gameId(avis.getGame() != null ? avis.getGame().getId() : null)
                .build();
    }

}
