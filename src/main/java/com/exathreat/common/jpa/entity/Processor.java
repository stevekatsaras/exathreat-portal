package com.exathreat.common.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@Table(name = "`processor`")
public class Processor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`acronym`", length = 10, nullable = false, unique = true)
	private String acronym;

	@Column(name = "`name`", length = 100, nullable = false)
	private String name;

	@Column(name = "`description`")
	private String description;

	@Column(name = "`available`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean available;
}