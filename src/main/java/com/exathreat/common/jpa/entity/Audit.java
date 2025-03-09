package com.exathreat.common.jpa.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
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
@Table(name = "`audit`")
public class Audit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`aud_code`", length = 60, nullable = false, unique = true)
	private String audCode;

	@Column(name = "`org_code`", length = 60)
	private String orgCode;

	@Column(name = "`auditor`", length = 100)
	private String auditor;

	@Column(name = "`operation`", length = 20)
	private String operation;

	@Column(name = "`entity_code`", length = 60)
	private String entityCode;

	@Column(name = "`entity_name`", length = 100)
	private String entityName;

	@Lob
	@Column(name = "`entity`")
	private String entity;

	@Column(name = "`audited`", nullable = false)
	private ZonedDateTime audited;
}