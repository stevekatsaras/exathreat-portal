package com.exathreat.organisation.insights;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OrganisationInsightsController {
	
	@GetMapping("/organisation/{orgCode}/insights")
	public ModelAndView initExplore(@PathVariable String orgCode, @RequestParam String page, ModelMap modelMap) throws Exception {
		return new ModelAndView("redirect:/organisation/" + orgCode + "/insights/" + page);
	}
}