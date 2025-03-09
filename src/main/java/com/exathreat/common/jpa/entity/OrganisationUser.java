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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
@Table(name = "`organisation_user`")
public class OrganisationUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`user_code`", length = 60, nullable = false, unique = true)
	private String userCode;

	@Column(name = "`email_address`", length = 100, nullable = false)
	@NotEmpty(message = "Email address is required")
	@Email(message = "Email address is invalid")
	private String emailAddress;

	@Column(name = "`given_name`", length = 100, nullable = false)
	@NotEmpty(message = "Given name is required")
	private String givenName;

	@Column(name = "`surname`", length = 100, nullable = false)
	@NotEmpty(message = "Surname is required")
	private String surname;

	@Column(name = "`telephone`", length = 50)
	private String telephone;

	@Column(name = "`mobile`", length = 50)
	private String mobile;

	@Column(name = "`user_owner`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean userOwner;

	@Column(name = "`user_role`", length = 20, nullable = false)
	@NotNull(message = "User role is required")
	private String userRole;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;

	@ManyToOne
	@JoinColumn(name = "`organisation_id`")
	private Organisation organisation;
}