package com.gamesUP.gamesUP.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avis {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String comment;

	@Column(nullable = false)
	private int note;

	@ManyToOne
	@JoinColumn(name = "game_id", nullable = false)
	private Game game;
}