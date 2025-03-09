package com.exathreat.organisation.settings.keys;

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
public class EditKeysSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private EditKeysSettingsService editKeysSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/settings/keys/{keyCode}/edit")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView initEditKey(@PathVariable String orgCode, @PathVariable String keyCode, EditKeysSettingsForm editKeysSettingsForm, ModelMap modelMap) throws Exception {
		editKeysSettingsService.initEditKey(orgCode, keyCode, editKeysSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/keys/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/keys/{keyCode}/edit")
	public ModelAndView doEditKey(@PathVariable String orgCode, @PathVariable String keyCode, @Valid EditKeysSettingsForm editKeysSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/keys/edit");
		}

		editKeysSettingsService.doEditKey(orgCode, keyCode, editKeysSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/keys?updated");
	}
}