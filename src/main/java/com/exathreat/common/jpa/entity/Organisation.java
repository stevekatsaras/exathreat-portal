package com.exathreat.common.jpa.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
@Table(name = "`organisation`")
public class Organisation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "`org_code`", length = 60, nullable = false, unique = true)
	private String orgCode;

	@Column(name = "`org_name`", length = 100, nullable = false)
	@NotEmpty(message = "Organisation name is required")
	private String orgName;

	@OneToOne
	@JoinColumn(name = "`business_type_id`", nullable = false)
	private BusinessType businessType;

	@Column(name = "`business_number`", length = 50)
	private String businessNumber;

	@Column(name = "`website`", length = 200)
	private String website;

	@Column(name = "`status`", length = 20, nullable = false)
	private String status;

	@Column(name = "`signup_date`", nullable = false)
	private ZonedDateTime signupDate;

	@Column(name = "`address1`", length = 100)
	private String address1;

	@Column(name = "`address2`", length = 100)
	private String address2;

	@Column(name = "`city`", length = 100)
	private String city;

	@Column(name = "`state`", length = 100)
	private String state;

	@Column(name = "`postcode`", length = 20)
	@NotEmpty(message = "Postcode is required")
	private String postcode;

	@OneToOne
	@JoinColumn(name = "`country_id`", nullable = false)
	private Country country;
		
	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;
	
	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;
}