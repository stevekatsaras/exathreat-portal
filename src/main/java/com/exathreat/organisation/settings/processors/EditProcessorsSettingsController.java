package com.exathreat.organisation.settings.processors;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EditProcessorsSettingsController {
	
	@Autowired
	private AccessService accessService;

	@Autowired
	private EditProcessorsSettingsService editProcessorsSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/processors/edit")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView initEditProcessors(@PathVariable String orgCode, EditProcessorsSettingsForm editProcessorsSettingsForm, ModelMap modelMap) throws Exception {
		editProcessorsSettingsService.initEditProcessors(orgCode, editProcessorsSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/processors/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/processors/edit")
	public ModelAndView doEditProcessors(@PathVariable String orgCode, EditProcessorsSettingsForm editProcessorsSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		editProcessorsSettingsService.doEditProcessors(orgCode, editProcessorsSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/processors?updated");
	}
}