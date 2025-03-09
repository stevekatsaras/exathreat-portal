package com.exathreat.organisation.index;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OrganisationIndexController {
	
	@Autowired
	private AccessService accessService;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
	}

	@GetMapping("/")
	public ModelAndView index() throws Exception {
		return new ModelAndView("index");
	}
}