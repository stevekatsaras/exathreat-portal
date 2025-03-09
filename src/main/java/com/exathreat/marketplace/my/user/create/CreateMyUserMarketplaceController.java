package com.exathreat.marketplace.my.user.create;

import javax.validation.Valid;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CreateMyUserMarketplaceController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private CreateMyUserMarketplaceService createMyUserMarketplaceService;

	@Autowired
	private CreateMyUserMarketplaceValidator createMyUserMarketplaceValidator;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
	}

	@GetMapping("/marketplace/my/user/create")
	public ModelAndView initCreate(CreateMyUserMarketplaceForm createMyUserMarketplaceForm) throws Exception {
		return new ModelAndView("marketplace/my/user/create");
	}

	@PostMapping("/marketplace/my/user/create")
	public ModelAndView doCreate(@Valid CreateMyUserMarketplaceForm createMyUserMarketplaceForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		createMyUserMarketplaceValidator.validateMarketplaceUser(createMyUserMarketplaceForm.getMarketplaceUser(), modelMap, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ModelAndView("marketplace/my/user/create");
		}
		createMyUserMarketplaceService.doCreate(createMyUserMarketplaceForm);
		return new ModelAndView("redirect:/marketplace/?userCreated");
	}
	
}