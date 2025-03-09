package com.exathreat.common.jpa.entity;

import java.time.ZonedDateTime;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.exathreat.common.jpa.converter.MapConverter;
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
@Table(name = "`organisation_notification`")
public class OrganisationNotification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`not_code`", length = 60, nullable = false, unique = true)
	private String notCode;

	@Column(name = "`name`", length = 100, nullable = false)
	@NotEmpty(message = "Name is required")
	private String name;

	@Column(name = "`description`")
	private String description;

	@Column(name = "`type`", length = 20, nullable = false)
	@NotEmpty(message = "Type is required")
	private String type;

	@Column(name = "`type_label`", length = 50, nullable = false)
	private String typeLabel;

	@Column(name = "`enabled`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean enabled;

	@Column(name = "`settings`", nullable = false, columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> settings;
	
	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;

	@ManyToOne
	@JoinColumn(name = "`organisation_id`")
	private Organisation organisation;
}