package com.exathreat.organisation.settings.keys;

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
public class ListKeysSettingsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ListKeysSettingsService listKeysSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/keys")
	public ModelAndView initKeys(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listKeysSettingsService.initKeys(orgCode, modelMap);
		return new ModelAndView("organisation/settings/keys/list");
	}

	@GetMapping("/organisation/{orgCode}/settings/keys/{keyCode}/disable")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView doDisableKey(@PathVariable String orgCode, @PathVariable String keyCode, ModelMap modelMap) throws Exception {
		listKeysSettingsService.doDisableKey(orgCode, keyCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/keys?disabled");
	}

	@GetMapping("/organisation/{orgCode}/settings/keys/{keyCode}/enable")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	public ModelAndView doEnableKey(@PathVariable String orgCode, @PathVariable String keyCode, ModelMap modelMap) throws Exception {
		listKeysSettingsService.doEnableKey(orgCode, keyCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/keys?enabled");
	}

	@GetMapping("/organisation/{orgCode}/settings/keys/{keyCode}/delete")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView doDeleteKey(@PathVariable String orgCode, @PathVariable String keyCode, ModelMap modelMap) throws Exception {
		listKeysSettingsService.doDeleteKey(orgCode, keyCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/keys?deleted");
	}
}
