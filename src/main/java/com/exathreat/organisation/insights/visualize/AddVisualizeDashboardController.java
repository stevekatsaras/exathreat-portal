package com.exathreat.organisation.insights.visualize;

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
public class AddVisualizeDashboardController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private AddVisualizeDashboardService addVisualizeDashboardService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/insights/visualize/add")
	public ModelAndView initInsightsAddVisualization(@PathVariable String orgCode, AddVisualizeDashboardForm addVisualizeDashboardForm, ModelMap modelMap) throws Exception {
		addVisualizeDashboardService.initInsightsAddVisualization(orgCode, addVisualizeDashboardForm);
		return new ModelAndView("organisation/insights/visualize/add");
	}

	@PostMapping("/organisation/{orgCode}/insights/visualize/add")
	public ModelAndView doInsightsAddVisualization(@PathVariable String orgCode, @Valid AddVisualizeDashboardForm addVisualizeDashboardForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/insights/visualize/add");
		}
		addVisualizeDashboardService.doInsightsAddVisualization(orgCode, addVisualizeDashboardForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/insights/visualize?added");
	}
}