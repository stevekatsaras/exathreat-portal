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
@Table(name = "`marketplace_user`")
public class MarketplaceUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "`email_address`", unique = true, length = 100, nullable = false)
	private String emailAddress;

	@Column(name = "`given_name`", length = 100, nullable = false)
	private String givenName;

	@Column(name = "`surname`", length = 100, nullable = false)
	private String surname;

	@Column(name = "`username`", unique = true, length = 100, nullable = false)
	@NotEmpty(message = "Username is required")
	private String username;
	
	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;
}