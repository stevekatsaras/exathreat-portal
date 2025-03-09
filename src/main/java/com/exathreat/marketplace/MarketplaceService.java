package com.exathreat.marketplace;

import java.util.Map;

import com.exathreat.common.jpa.entity.enums.MarketplaceItemStateEnum;
import com.exathreat.common.jpa.repository.MarketplaceItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class MarketplaceService {

	@Autowired
	private MarketplaceItemRepository marketplaceItemRepository;
	
	@Transactional(readOnly = true)
	public Map<String, Object> initMarketplaceItems(ModelMap modelMap) throws Exception {
		return Map.of("success", true, "results", marketplaceItemRepository.findByStateOrderByCreatedDesc(MarketplaceItemStateEnum.Advertise.name()));
	}
}