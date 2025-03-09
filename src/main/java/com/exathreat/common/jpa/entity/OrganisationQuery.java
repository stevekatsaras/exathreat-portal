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
@Table(name = "`organisation_query`")
public class OrganisationQuery {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`query_code`", length = 60, nullable = false, unique = true)
	private String queryCode;

	@Column(name = "`name`", length = 100, nullable = false)
	@NotEmpty(message = "Name is required")
	private String name;

	@Column(name = "`query_str`")
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

	@Column(name = "`interval_unit`", length = 10)
	@NotEmpty(message = "Interval is required")
	private String intervalUnit;

	@Column(name = "`min_doc_count`")
	private Integer minDocCount;

	@Column(name = "`enabled`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean enabled;

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