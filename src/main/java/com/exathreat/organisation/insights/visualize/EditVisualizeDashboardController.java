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
public class EditVisualizeDashboardController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private EditVisualizeDashboardService editVisualizeDashboardService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/edit")
	public ModelAndView initInsightsEditVisualization(@PathVariable String orgCode, @PathVariable String vizCode, EditVisualizeDashboardForm editVisualizeDashboardForm, ModelMap modelMap) throws Exception {
		editVisualizeDashboardService.initInsightsEditVisualization(orgCode, vizCode, editVisualizeDashboardForm);
		return new ModelAndView("organisation/insights/visualize/edit");
	}

	@PostMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/edit")
	public ModelAndView doInsightsEditVisualization(@PathVariable String orgCode, @PathVariable String vizCode, @Valid EditVisualizeDashboardForm editVisualizeDashboardForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/insights/visualize/edit");
		}
		editVisualizeDashboardService.doInsightsEditVisualization(orgCode, vizCode, editVisualizeDashboardForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/insights/visualize?updated");
	}
}