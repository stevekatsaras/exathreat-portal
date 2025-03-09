package com.exathreat.organisation.settings.queries;

import javax.validation.Valid;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class EditQueriesSettingsController {
	
	@Autowired
	private AccessService accessService;

	@Autowired
	private EditQueriesSettingService editQueriesSettingService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		editQueriesSettingService.getQueriesMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/queries/{queryCode}/edit")
	public ModelAndView initEditQuery(@PathVariable String orgCode, @PathVariable String queryCode, EditQueriesSettingsForm editQueriesSettingsForm, ModelMap modelMap) throws Exception {
		editQueriesSettingService.initEditQuery(orgCode, queryCode, editQueriesSettingsForm, modelMap);
		return new ModelAndView("organisation/settings/queries/edit");
	}

	@PostMapping("/organisation/{orgCode}/settings/queries/{queryCode}/edit")
	public ModelAndView doEditQuery(@PathVariable String orgCode, @PathVariable String queryCode, @Valid EditQueriesSettingsForm editQueriesSettingsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/settings/queries/edit");
		}
		editQueriesSettingService.doEditQuery(orgCode, queryCode, editQueriesSettingsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/queries?updated");
	}
}