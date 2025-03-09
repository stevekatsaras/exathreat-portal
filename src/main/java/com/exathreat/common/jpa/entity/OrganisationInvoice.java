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
@Table(name = "`organisation_invoice`")
public class OrganisationInvoice {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`inv_code`", length = 60, nullable = false, unique = true)
	private String invCode;

	@Column(name = "`period_from`")
	private ZonedDateTime periodFrom;

	@Column(name = "`period_to`")
	private ZonedDateTime periodTo;

	@Column(name = "`status`", length = 20, nullable = false)
	private String status;

	@Column(name = "`date_due`")
	private ZonedDateTime dateDue;

	@Column(name = "`event_total`")
	private Long eventTotal; // total number of events

	@Column(name = "`data_ingest_total`")
	private Long dataIngestTotal; // total bytes of all events

	@Column(name = "`data_50`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean data50;	// 50% data threshold (free subscriptions)

	@Column(name = "`data_75`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean data75; // 75% data threshold (free subscriptions)

	@Column(name = "`data_100`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean data100; // 100% data threshold (free subscriptions)

	@Column(name = "`amount_total`")
	private Long amountTotal;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;

	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;

	@ManyToOne
	@JoinColumn(name = "`organisation_subscription_id`")
	private OrganisationSubscription organisationSubscription;
}