package com.exathreat.marketplace;

import java.util.Map;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class MarketplaceController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private MarketplaceService marketplaceService;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
	}

	@GetMapping("/marketplace")
	public ModelAndView initMarketplace(ModelMap modelMap) throws Exception {
		return new ModelAndView("marketplace/list");
	}

	@GetMapping(value = "/marketplace/items")
	public @ResponseBody Map<String, Object> initMarketplaceItems(ModelMap modelMap) throws Exception {
		return marketplaceService.initMarketplaceItems(modelMap);
	}
}