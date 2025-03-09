package com.exathreat.organisation.insights.visualize;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ListVisualizeInsightsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ListVisualizeInsightsService listVisualizeInsightsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/insights/visualize")
	public ModelAndView initInsightsVisualize(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listVisualizeInsightsService.initInsightsVisualize(orgCode, modelMap);
		return new ModelAndView("organisation/insights/visualize/list");
	}

	@GetMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/run")
	public ModelAndView initInsightsRunVisualization(@PathVariable String orgCode, @PathVariable String vizCode, ModelMap modelMap) throws Exception {
		listVisualizeInsightsService.initInsightsRunVisualization(orgCode, vizCode, modelMap);
		return new ModelAndView("organisation/insights/visualize/run");
	}
	
	@GetMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/delete")
	public ModelAndView doInsightsDeleteVisualization(@PathVariable String orgCode, @PathVariable String vizCode, ModelMap modelMap) throws Exception {
		listVisualizeInsightsService.doInsightsDeleteVisualization(orgCode, vizCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/insights/visualize?deleted");
	}
}