package com.exathreat.organisation.settings.users;

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
public class EditUsersSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private EditUsersSettingsService editUsersSettingsService;

	@Autowired
	private UsersSettingsValidator usersSettingsValidator;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		editUsersSettingsService.getUserMetadata(modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/settings/users/{userCode}/edit")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView initEditUser(@PathVariable String orgCode, @PathVariable String userCode, EditUsersSettingsForm editUsersSettingsForm, ModelMap modelMap) throws Exception {
		editUsersSettingsService.initEditUser(orgCode, userCode, editUsersSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/users/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/users/{userCode}/edit")
	public ModelAndView doEditUser(@PathVariable String orgCode, @PathVariable String userCode, @Valid EditUsersSettingsForm editUsersSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		usersSettingsValidator.validateUserRole(orgCode, userCode, editUsersSettingsForm, modelMap, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/users/edit");
		}
		editUsersSettingsService.doEditUser(orgCode, userCode, editUsersSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/users?updated");
	}
}