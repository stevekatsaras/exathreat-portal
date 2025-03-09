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
@Table(name = "`organisation_detection`")
public class OrganisationDetection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`det_code`", length = 60, nullable = false, unique = true)
	private String detCode;

	@Column(name = "`name`", length = 100, nullable = false)
	@NotEmpty(message = "Name is required")
	private String name;

	@Column(name = "`description`")
	private String description;

	@Column(name = "`query_str`", nullable = false, columnDefinition = "TEXT")
	@NotEmpty(message = "Query is required")
	private String queryStr;

	@Column(name = "`index_alias`", length = 50)
	@NotEmpty(message = "Index is required")
	private String indexAlias;

	@Column(name = "`date_range`", length = 100)
	@NotEmpty(message = "Date range is required")
	private String dateRange;

	@Column(name = "`date_format`", length = 100)
	private String dateFormat;

	@Column(name = "`time_format`", length = 100)
	private String timeFormat;

	@Column(name = "`severity`", nullable = false)
	private Integer severity;

	@Column(name = "`risk_score`", nullable = false)
	private Integer riskScore;

	@Column(name = "`runtime`", nullable = false)
	private Integer runtime;

	@Column(name = "`runtime_unit`", length = 20, nullable = false)
	private String runtimeUnit;

	@Column(name = "`version`", nullable = false)
	private Integer version;

	@Column(name = "`enabled`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean enabled;

	@Column(name = "`references`", columnDefinition = "TEXT")
	private String references;

	@Column(name = "`annotations`", columnDefinition = "TEXT")
	private String annotations;

	@Column(name = "`tags`", columnDefinition = "TEXT")
	private String tags;

	@Column(name = "`mitre_attack`", nullable = false, columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> mitreAttack;

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