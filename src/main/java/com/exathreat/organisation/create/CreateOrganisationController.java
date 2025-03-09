package com.exathreat.organisation.create;

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
public class CreateOrganisationController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private CreateOrganisationService createOrganisationService;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		createOrganisationService.getOrganisationMetadata(modelMap);
	}

	@GetMapping("/organisation/create")
	public ModelAndView initCreate(CreateOrganisationForm createOrganisationForm) throws Exception {
		createOrganisationService.initCreate(createOrganisationForm);
		return new ModelAndView("organisation/create");
	}

	@PostMapping("/organisation/create")
	public ModelAndView doCreate(@Valid CreateOrganisationForm createOrganisationForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/create");
		}
		String orgCode = createOrganisationService.doCreate(createOrganisationForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "?created");
	}
}