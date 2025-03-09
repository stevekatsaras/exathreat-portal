package com.exathreat.organisation.forensics.threats;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ThreatsForensicsController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private ThreatsForensicsService threatsForensicsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/forensics/threats")
	public ModelAndView initForensicsThreats(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		return new ModelAndView("organisation/forensics/threats/show");
	}

	@PostMapping(value = {"/organisation/{orgCode}/forensics/threats/data/load/{action}"})
	public @ResponseBody ThreatsForensicsPanelData loadData(@PathVariable String orgCode, @PathVariable String action, @RequestBody ThreatsForensicsPanelData threatsForensicsPanelData, ModelMap modelMap) throws Exception {
		return threatsForensicsService.loadData(orgCode, action, threatsForensicsPanelData, modelMap);
	}
}