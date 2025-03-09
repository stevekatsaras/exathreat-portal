package com.exathreat.organisation.settings.indexes;

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
public class EditIndexSettingsController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private EditIndexSettingsService editIndexSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/indexes/{indCode}/edit")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView initEditIndex(@PathVariable String orgCode, @PathVariable String indCode, EditIndexSettingsForm editIndexSettingsForm, ModelMap modelMap) throws Exception {
		editIndexSettingsService.initEditIndex(orgCode, indCode, editIndexSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/indexes/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/indexes/{indCode}/edit")
	public ModelAndView doEditIndex(@PathVariable String orgCode, @PathVariable String indCode, @Valid EditIndexSettingsForm editIndexSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/indexes/edit");
		}
		editIndexSettingsService.doEditIndex(orgCode, indCode, editIndexSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/indexes?updated");
	}
}