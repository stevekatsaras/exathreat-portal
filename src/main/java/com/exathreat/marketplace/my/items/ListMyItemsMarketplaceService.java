package com.exathreat.marketplace.my.items;

import com.exathreat.common.jpa.entity.MarketplaceItem;
import com.exathreat.common.jpa.entity.MarketplaceUser;
import com.exathreat.common.jpa.repository.MarketplaceItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListMyItemsMarketplaceService {

	@Autowired
	private MarketplaceItemRepository marketplaceItemRepository;

	@Transactional(readOnly = true)
	public void initMyMarketplaceItems(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("marketplaceItems", marketplaceItemRepository.findByMarketplaceUser((MarketplaceUser) modelMap.get("marketplaceUser")));
	}

	@Transactional(readOnly = false)
	public void doDeleteMyMarketplaceItem(String itemCode, ModelMap modelMap) throws Exception {
		MarketplaceItem marketplaceItem = marketplaceItemRepository.findByItemCode(itemCode);
		marketplaceItemRepository.delete(marketplaceItem);
	}
}