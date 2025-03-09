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
@Table(name = "`currency`")
public class Currency {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`iso3`", length = 10, nullable = false, unique = true)
	private String iso3;

	@Column(name = "`symbol`", length = 5, nullable = false)
	private String symbol;

	@Column(name = "`icon`", length = 20)
	private String icon;

	@Column(name = "`divisible`", nullable = false)
	private Integer divisible;
}