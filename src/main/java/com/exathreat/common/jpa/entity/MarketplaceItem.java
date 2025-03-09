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
import javax.persistence.OneToOne;
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
@Table(name = "`marketplace_item`")
public class MarketplaceItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`item_code`", length = 60, nullable = false, unique = true)
	private String itemCode;

	@OneToOne
	@JoinColumn(name = "`marketplace_category_id`", nullable = false)
	private MarketplaceCategory marketplaceCategory;

	@OneToOne
	@JoinColumn(name = "`marketplace_user_id`", nullable = false)
	private MarketplaceUser marketplaceUser;

	@Column(name = "`item`", nullable = false, columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> item;

	@Column(name = "`state`", length = 20, nullable = false)
	@NotEmpty(message = "State is required")
	private String state;

	@Column(name = "`created`", nullable = false)
	private ZonedDateTime created;
	
	@Column(name = "`modified`", nullable = false)
	private ZonedDateTime modified;
}