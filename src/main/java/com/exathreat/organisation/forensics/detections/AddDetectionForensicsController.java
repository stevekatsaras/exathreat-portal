package com.exathreat.organisation.forensics.detections;

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
public class AddDetectionForensicsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private AddDetectionForensicsService addDetectionForensicsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		addDetectionForensicsService.getDetectionMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections/add")
	public ModelAndView initForensicsAddDetection(@PathVariable String orgCode, AddDetectionForensicsForm addDetectionForensicsForm, ModelMap modelMap) throws Exception {
		addDetectionForensicsService.initForensicsAddDetection(orgCode, addDetectionForensicsForm, modelMap);
		return new ModelAndView("organisation/forensics/detections/add");
	}

	@PostMapping("/organisation/{orgCode}/forensics/detections/add")
	public ModelAndView doForensicsAddDetection(@PathVariable String orgCode, @Valid AddDetectionForensicsForm addDetectionForensicsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/forensics/detections/add");
		}
		addDetectionForensicsService.doForensicsAddDetection(orgCode, addDetectionForensicsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/detections?added");
	}
	
}