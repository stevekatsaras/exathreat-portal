package com.exathreat.common.jpa.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.exathreat.common.service.AuditService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@EntityListeners({AuditService.class})
@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@Table(name = "`organisation_key`")
public class OrganisationKey {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`key_code`", length = 60, nullable = false, unique = true)
	private String keyCode;

	@Column(name = "`name`", length = 100, nullable = false)
	@NotEmpty(message = "Name is required")
	private String name;

	@Column(name = "`description`")
	private String description;
	
	@Column(name = "`key_type`", length = 20, nullable = false)
	private String keyType;

	@Column(name = "`enabled`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean enabled;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;

	@ManyToOne
	@JoinColumn(name = "`organisation_id`")
	private Organisation organisation;
}