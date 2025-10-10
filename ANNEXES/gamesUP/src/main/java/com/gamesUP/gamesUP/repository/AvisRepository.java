package com.gamesUP.gamesUP.repository;

import com.gamesUP.gamesUP.entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvisRepository extends JpaRepository<Avis, Long> {
}