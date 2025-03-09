package com.exathreat.marketplace.my.user.create;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.MarketplaceUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class CreateMyUserMarketplaceForm {
	@Valid
	private MarketplaceUser marketplaceUser;
}