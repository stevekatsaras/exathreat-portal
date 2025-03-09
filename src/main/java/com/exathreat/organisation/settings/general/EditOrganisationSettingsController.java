package com.exathreat.organisation.settings.general;

import javax.validation.Valid;

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
public class EditOrganisationSettingsController {
	
	@Autowired
	private AccessService accessService;

	@Autowired
	private EditOrganisationSettingsService editOrganisationSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		editOrganisationSettingsService.getOrganisationMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/general/edit")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView initEditGeneralSettings(@PathVariable String orgCode, EditOrganisationSettingsForm editOrganisationSettingsForm, ModelMap modelMap) throws Exception {
		editOrganisationSettingsService.initEditGeneralSettings(orgCode, editOrganisationSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/general/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/general/edit")
	public ModelAndView doEditGeneralSettings(@PathVariable String orgCode, @Valid EditOrganisationSettingsForm editOrganisationSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/general/edit");
		}
		editOrganisationSettingsService.doEditGeneralSettings(orgCode, editOrganisationSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/general?updated");
	}

	@GetMapping("/organisation/{orgCode}/settings/general/deactivate")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView doDeactivate(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		editOrganisationSettingsService.doDeactivate(orgCode, modelMap);
		return new ModelAndView("redirect:/?deactivated");
	}

}