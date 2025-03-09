package com.exathreat.marketplace.my.items;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import com.exathreat.common.jpa.entity.MarketplaceItem;
import com.exathreat.common.jpa.entity.enums.MarketplaceItemStateEnum;
import com.exathreat.common.jpa.repository.MarketplaceItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditMyItemsMarketplaceService {

	@Autowired
	private MarketplaceItemRepository marketplaceItemRepository;

	public void getMyMarketplaceItemMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("itemStates", Arrays.asList(MarketplaceItemStateEnum.values()));
	}

	@Transactional(readOnly = true)
	public void initMyMarketplaceEditItem(String itemCode, EditMyItemsMarketplaceForm editMyItemsMarketplaceForm, ModelMap modelMap) throws Exception {
		editMyItemsMarketplaceForm.setMarketplaceItem(marketplaceItemRepository.findByItemCode(itemCode));
	}

	@Transactional(readOnly = false)
	public void doMyMarketplaceEditItem(String itemCode, EditMyItemsMarketplaceForm editMyItemsMarketplaceForm, ModelMap modelMap) throws Exception {
		MarketplaceItem marketplaceItemDto = editMyItemsMarketplaceForm.getMarketplaceItem();

		MarketplaceItem marketplaceItem = marketplaceItemRepository.findByItemCode(itemCode);
		marketplaceItem.setState(marketplaceItemDto.getState());
		marketplaceItem.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		marketplaceItemRepository.saveAndFlush(marketplaceItem);
	}
}