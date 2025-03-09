package com.exathreat.marketplace.my.items;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ListMyItemsMarketplaceController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ListMyItemsMarketplaceService listMyItemsMarketplaceService;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
	}

	@GetMapping("/marketplace/my/items")
	public ModelAndView initMyMarketplaceItems(ModelMap modelMap) throws Exception {
		listMyItemsMarketplaceService.initMyMarketplaceItems(modelMap);
		return new ModelAndView("marketplace/my/items/list");
	}

	@GetMapping("/marketplace/my/items/{itemCode}/delete")
	public ModelAndView doDeleteMyMarketplaceItem(@PathVariable String itemCode, ModelMap modelMap) throws Exception {
		listMyItemsMarketplaceService.doDeleteMyMarketplaceItem(itemCode, modelMap);
		return new ModelAndView("redirect:/marketplace/my/items?deleted");
	}
}