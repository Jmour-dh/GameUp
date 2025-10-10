package com.gamesUP.gamesUP.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL)
	private List<PurchaseLine> line;

	@Column(nullable = false)
	private Date date;

	@Column(nullable = false)
	private boolean paid;

	@Column(nullable = false)
	private boolean delivered;

	@Column(nullable = false)
	private boolean archived;
}