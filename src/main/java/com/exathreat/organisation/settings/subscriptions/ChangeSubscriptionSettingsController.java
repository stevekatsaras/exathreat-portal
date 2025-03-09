package com.exathreat.organisation.settings.subscriptions;

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
public class ChangeSubscriptionSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ChangeSubscriptionSettingsService changeSubscriptionSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		changeSubscriptionSettingsService.getSubscriptionMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/subscriptions/change")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView initChangeSubscription(@PathVariable String orgCode, ChangeSubscriptionSettingsForm changeSubscriptionSettingsForm, ModelMap modelMap) throws Exception {
		changeSubscriptionSettingsService.initChangeSubscription(orgCode, changeSubscriptionSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/subscriptions/change");
	}

	@PostMapping("/organisation/{orgCode}/settings/subscriptions/change")
	public ModelAndView doChangeSubscription(@PathVariable String orgCode, @Valid ChangeSubscriptionSettingsForm changeSubscriptionSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/subscriptions/change");
		}
		
		String outcome = changeSubscriptionSettingsService.doChangeSubscription(orgCode, changeSubscriptionSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/subscriptions?" + outcome);
	}
}