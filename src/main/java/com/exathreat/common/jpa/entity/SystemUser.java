package com.exathreat.common.jpa.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "`system_user`")
public class SystemUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "`email_address`", unique = true, length = 100, nullable = false)
	private String emailAddress;

	@Column(name = "`date_format`", length = 100, nullable = false)
	@NotEmpty(message = "Date format is required")
	private String dateFormat;

	@Column(name = "`time_format`", length = 100, nullable = false)
	@NotEmpty(message = "Time format is required")
	private String timeFormat;

	@Column(name = "`timezone`", length = 100, nullable = false)
	@NotEmpty(message = "Timezone is required")
	private String timezone;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;
}