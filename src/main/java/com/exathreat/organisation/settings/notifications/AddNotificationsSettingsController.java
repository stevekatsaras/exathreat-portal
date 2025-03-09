package com.exathreat.organisation.settings.notifications;

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
public class AddNotificationsSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private AddNotificationsSettingsService addNotificationsSettingsService;

	@Autowired
	private NotificationSettingsValidator notificationSettingsValidator;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		addNotificationsSettingsService.getNotificationMetadata(modelMap);
	}
		
	@GetMapping("/organisation/{orgCode}/settings/notifications/add")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView initAddNotification(@PathVariable String orgCode, AddNotificationsSettingsForm addNotificationsSettingsForm, ModelMap modelMap) throws Exception {
		addNotificationsSettingsService.initAddNotification(addNotificationsSettingsForm);
		return new ModelAndView("organisation/settings/notifications/add");
	}

	@PostMapping("/organisation/{orgCode}/settings/notifications/add")
	public ModelAndView doAddNotification(@PathVariable String orgCode, @Valid AddNotificationsSettingsForm addNotificationsSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		notificationSettingsValidator.validateNotificationSettings(orgCode, addNotificationsSettingsForm.getOrganisationNotification(), modelMap, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/notifications/add");
		}

		if ("Test".equals(addNotificationsSettingsForm.getAction())) {
			notificationSettingsValidator.test(orgCode, addNotificationsSettingsForm.getOrganisationNotification(), modelMap, bindingResult);
			return new ModelAndView("organisation/settings/notifications/add");
		}

		addNotificationsSettingsService.doAddNotification(orgCode, addNotificationsSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/notifications?added");
	}
}