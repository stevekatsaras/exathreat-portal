package com.exathreat.organisation.settings.users;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ListUsersSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ListUsersSettingsService listUsersSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/users")
	public ModelAndView initUsers(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listUsersSettingsService.initUsers(orgCode, modelMap);
		return new ModelAndView("organisation/settings/users/list");
	}

	@GetMapping("/organisation/{orgCode}/settings/users/{userCode}/remove")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView doRemoveUser(@PathVariable String orgCode, @PathVariable String userCode, ModelMap modelMap) throws Exception {
		listUsersSettingsService.doRemoveUser(orgCode, userCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/users?removed");
	}

	@GetMapping("/organisation/{orgCode}/settings/users/{userCode}/invitation/resend")
	public ModelAndView resendUserInvitation(@PathVariable String orgCode, @PathVariable String userCode, ModelMap modelMap) throws Exception {
		listUsersSettingsService.resendUserInvitation(orgCode, userCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/users?resent");
	}
}