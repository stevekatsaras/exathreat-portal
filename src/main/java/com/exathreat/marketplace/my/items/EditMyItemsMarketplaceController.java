package com.exathreat.marketplace.my.items;

import javax.validation.Valid;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EditMyItemsMarketplaceController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private EditMyItemsMarketplaceService editMyItemsMarketplaceService;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		editMyItemsMarketplaceService.getMyMarketplaceItemMetadata(modelMap);
	}

	@GetMapping("/marketplace/my/items/{itemCode}/edit")
	public ModelAndView initMyMarketplaceEditItem(@PathVariable String itemCode, EditMyItemsMarketplaceForm editMyItemsMarketplaceForm, ModelMap modelMap) throws Exception {
		editMyItemsMarketplaceService.initMyMarketplaceEditItem(itemCode, editMyItemsMarketplaceForm, modelMap);
		return new ModelAndView("marketplace/my/items/edit");
	}

	@PostMapping("/marketplace/my/items/{itemCode}/edit")
	public ModelAndView doMyMarketplaceEditItem(@PathVariable String itemCode, @Valid EditMyItemsMarketplaceForm editMyItemsMarketplaceForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("marketplace/my/items/edit");
		}
		editMyItemsMarketplaceService.doMyMarketplaceEditItem(itemCode, editMyItemsMarketplaceForm, modelMap);
		return new ModelAndView("redirect:/marketplace/my/items?updated");
	}
	
}