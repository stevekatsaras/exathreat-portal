package com.exathreat.organisation.settings.notifications;

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
public class ListNotificationsSettingsController {
	
	@Autowired
	private AccessService accessService;

	@Autowired ListNotificationsSettingsService listNotificationsSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/notifications")
	public ModelAndView initNotifications(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listNotificationsSettingsService.initNotifications(orgCode, modelMap);
		return new ModelAndView("organisation/settings/notifications/list");
	}

	@GetMapping("/organisation/{orgCode}/settings/notifications/{notCode}/disable")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView doDisableNotification(@PathVariable String orgCode, @PathVariable String notCode, ModelMap modelMap) throws Exception {
		listNotificationsSettingsService.doDisableNotification(orgCode, notCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/notifications?disabled");
	}

	@GetMapping("/organisation/{orgCode}/settings/notifications/{notCode}/enable")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView doEnableNotification(@PathVariable String orgCode, @PathVariable String notCode, ModelMap modelMap) throws Exception {
		listNotificationsSettingsService.doEnableNotification(orgCode, notCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/notifications?enabled");
	}

	@GetMapping("/organisation/{orgCode}/settings/notifications/{notCode}/delete")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView doDeleteNotification(@PathVariable String orgCode, @PathVariable String notCode, ModelMap modelMap) throws Exception {
		listNotificationsSettingsService.doDeleteNotification(orgCode, notCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/notifications?deleted");
	}
}