package com.exathreat.organisation.settings.indexes;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ListIndexesSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ListIndexesSettingsService listIndexesSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/indexes")
	public ModelAndView initIndexes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listIndexesSettingsService.initIndexes(orgCode, modelMap);
		return new ModelAndView("organisation/settings/indexes/list");
	}
}