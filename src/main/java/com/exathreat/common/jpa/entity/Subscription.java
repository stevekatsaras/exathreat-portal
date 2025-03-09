package com.exathreat.common.jpa.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.exathreat.common.jpa.converter.MapConverter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
@Table(name = "`subscription`")
public class Subscription {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "`model`", length = 20, nullable = false)
	private String model;

	@Column(name = "`name`", length = 100, nullable = false)
	private String name;

	@Column(name = "`description`", length = 200)
	private String description;

	@Column(name = "`details`", nullable = false, columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> details;

	@Column(name = "`price_period`", length = 20, nullable = false)
	private String pricePeriod;

	@Column(name = "`price_amount`", nullable = false)
	private Long priceAmount;

	@Column(name = "`data_amount`", nullable = false)
	private Long dataAmount;
	
	@Column(name = "`data_excess`", nullable = false, columnDefinition = "TEXT")
	@Convert(converter = MapConverter.class)
	private Map<String, Object> dataExcess;

	@Column(name = "`enabled`", nullable = false, columnDefinition = "BIT", length = 1)
	private Boolean enabled;

	@OneToOne
	@JoinColumn(name = "`currency_id`")
	private Currency currency;

	@OneToOne
	@JoinColumn(name = "`data_unit_id`")
	private DataUnit dataUnit;
}