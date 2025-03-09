package com.exathreat.organisation.settings.queries;

import java.util.Map;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ListQueriesSettingsController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private ListQueriesSettingsService listQueriesSettingsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/settings/queries")
	public ModelAndView initQueries(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listQueriesSettingsService.initQueries(orgCode, modelMap);
		return new ModelAndView("organisation/settings/queries/list");
	}

	@PostMapping(value = "/organisation/{orgCode}/settings/queries/{queryCode}/clone", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> cloneQuery(@PathVariable String orgCode, @PathVariable String queryCode, @RequestBody Map<String, Object> params, ModelMap modelMap) throws Exception {
		listQueriesSettingsService.doCloneQuery(orgCode, queryCode, params, modelMap);
		return params;
	}

	@GetMapping("/organisation/{orgCode}/settings/queries/{queryCode}/disable")
	public ModelAndView doDisableQuery(@PathVariable String orgCode, @PathVariable String queryCode, ModelMap modelMap) throws Exception {
		listQueriesSettingsService.doDisableQuery(orgCode, queryCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/queries?disabled");
	}

	@GetMapping("/organisation/{orgCode}/settings/queries/{queryCode}/enable")
	public ModelAndView doEnableQuery(@PathVariable String orgCode, @PathVariable String queryCode, ModelMap modelMap) throws Exception {
		listQueriesSettingsService.doEnableQuery(orgCode, queryCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/queries?enabled");
	}

	@GetMapping("/organisation/{orgCode}/settings/queries/{queryCode}/delete")
	public ModelAndView doDeleteQuery(@PathVariable String orgCode, @PathVariable String queryCode, ModelMap modelMap) throws Exception {
		listQueriesSettingsService.doDeleteQuery(orgCode, queryCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/queries?deleted");
	}
}