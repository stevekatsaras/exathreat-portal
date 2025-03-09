package com.exathreat.organisation.settings.general;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ViewOrganisationSettingsController {

	@Autowired
	private AccessService accessService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/settings/general")
	public ModelAndView initGeneralSettings(@PathVariable String orgCode) throws Exception {
		return new ModelAndView("organisation/settings/general/view");
	}
}