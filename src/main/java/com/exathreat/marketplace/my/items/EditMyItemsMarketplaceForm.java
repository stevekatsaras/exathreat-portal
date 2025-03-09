package com.exathreat.marketplace.my.items;

import com.exathreat.common.jpa.entity.MarketplaceItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditMyItemsMarketplaceForm {
	private MarketplaceItem marketplaceItem;
}