package com.exathreat.organisation.settings;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OrganisationSettingsController {
	
	@GetMapping("/organisation/{orgCode}/settings")
	public ModelAndView initSettings(@PathVariable String orgCode, @RequestParam String page) throws Exception {
		return new ModelAndView("redirect:/organisation/" + orgCode + "/settings/" + page);
	}
	
}