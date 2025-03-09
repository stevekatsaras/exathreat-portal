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
public class AddKeysSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private AddKeysSettingsService addKeysSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/settings/keys/add")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView initAddKey(@PathVariable String orgCode, AddKeysSettingsForm addKeysSettingsForm, ModelMap modelMap) throws Exception {
		return new ModelAndView("organisation/settings/keys/add");
	}

	@PostMapping("/organisation/{orgCode}/settings/keys/add")
	public ModelAndView doAddKey(@PathVariable String orgCode, @Valid AddKeysSettingsForm addKeysSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/keys/add");
		}
		addKeysSettingsService.doAddKey(orgCode, addKeysSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/keys?added");
	}
}