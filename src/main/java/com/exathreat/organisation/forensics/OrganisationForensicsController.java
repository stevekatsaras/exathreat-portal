package com.exathreat.organisation.forensics;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OrganisationForensicsController {
	
	@GetMapping("/organisation/{orgCode}/forensics")
	public ModelAndView initForensics(@PathVariable String orgCode, @RequestParam String page, ModelMap modelMap) throws Exception {
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/" + page);
	}
}