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
@Table(name = "`organisation_index`")
public class OrganisationIndex {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`ind_code`", length = 60, nullable = false, unique = true)
	private String indCode;

	@Column(name = "`alias_name`", length = 50)
	private String aliasName;

	@Column(name = "`index_name`", length = 50)
	private String indexName;

	@Column(name = "`index_type`", length = 20, nullable = false)
	private String indexType;

	@Column(name = "`retention_days`")
	private Integer retentionDays;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;

	@ManyToOne
	@JoinColumn(name = "`organisation_id`")
	private Organisation organisation;
}