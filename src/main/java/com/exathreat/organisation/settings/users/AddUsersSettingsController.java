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
public class AddUsersSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private AddUsersSettingsService addUsersSettingsService;

	@Autowired
	private UsersSettingsValidator usersSettingsValidator;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		addUsersSettingsService.getUserMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/users/add")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView initAddUser(@PathVariable String orgCode, AddUsersSettingsForm addUsersSettingsForm, ModelMap modelMap) throws Exception {
		addUsersSettingsService.initAddUser(orgCode, addUsersSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/users/add");
	}

	@PostMapping("/organisation/{orgCode}/settings/users/add")
	public ModelAndView doAddUser(@PathVariable String orgCode, @Valid AddUsersSettingsForm addUsersSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		usersSettingsValidator.validateUserEmailAddress(orgCode, addUsersSettingsForm, modelMap, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/users/add");
		}

		addUsersSettingsService.doAddUser(orgCode, addUsersSettingsForm, modelMap);		
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/users?added");
	}
}