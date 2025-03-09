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
public class EditNotificationsSettingsController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private EditNotificationsSettingsService editNotificationsSettingsService;

	@Autowired
	private NotificationSettingsValidator notificationSettingsValidator;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		editNotificationsSettingsService.getNotificationMetadata(modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/settings/notifications/{notCode}/edit")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView initEditNotification(@PathVariable String orgCode, @PathVariable String notCode, EditNotificationsSettingsForm editNotificationsSettingsForm, ModelMap modelMap) throws Exception {
		editNotificationsSettingsService.initEditNotification(orgCode, notCode, editNotificationsSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/notifications/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/notifications/{notCode}/edit")
	public ModelAndView doEditNotification(@PathVariable String orgCode, @PathVariable String notCode, @Valid EditNotificationsSettingsForm editNotificationsSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		notificationSettingsValidator.validateNotificationSettings(orgCode, editNotificationsSettingsForm.getOrganisationNotification(), modelMap, bindingResult);
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/notifications/edit");
		}

		if ("Test".equals(editNotificationsSettingsForm.getAction())) {
			notificationSettingsValidator.test(orgCode, editNotificationsSettingsForm.getOrganisationNotification(), modelMap, bindingResult);
			return new ModelAndView("organisation/settings/notifications/edit");
		}

		editNotificationsSettingsService.doEditNotification(orgCode, notCode, editNotificationsSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/notifications?updated");
	}
}