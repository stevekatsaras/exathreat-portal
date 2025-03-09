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
@Table(name = "`organisation_visualization`")
public class OrganisationVisualization {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`viz_code`", length = 60, nullable = false, unique = true)
	private String vizCode;

	@Column(name = "`name`", length = 100, nullable = false)
	@NotEmpty(message = "Name is required")
	private String name;

	@Column(name = "`description`")
	private String description;

	@Column(name = "`date_range`", length = 100)
	private String dateRange;

	@Column(name = "`date_format`", length = 100)
	private String dateFormat;

	@Column(name = "`time_format`", length = 100)
	private String timeFormat;

	@Column(name = "`refresh`", length = 20)
	private String refresh;

	@Column(name = "`charts`", nullable = false, columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> charts;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;

	@ManyToOne
	@JoinColumn(name = "`organisation_user_id`")
	private OrganisationUser organisationUser;

	@ManyToOne
	@JoinColumn(name = "`organisation_id`")
	private Organisation organisation;
}