package com.exathreat.organisation.forensics.detections;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class ListDetectionsForensicsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ListDetectionsForensicsService listDetectionsForensicsService;
	
	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections")
	public ModelAndView initForensicsDetections(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		listDetectionsForensicsService.initForensicsDetections(orgCode, modelMap);
		return new ModelAndView("organisation/forensics/detections/list");
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections/{detCode}/disable")
	public ModelAndView doForensicsDisableDetection(@PathVariable String orgCode, @PathVariable String detCode, ModelMap modelMap) throws Exception {
		listDetectionsForensicsService.doForensicsDisableDetection(orgCode, detCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/detections?disabled");
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections/{detCode}/enable")
	public ModelAndView doForensicsEnableDetection(@PathVariable String orgCode, @PathVariable String detCode, ModelMap modelMap) throws Exception {
		listDetectionsForensicsService.doForensicsEnableDetection(orgCode, detCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/detections?enabled");
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections/{detCode}/delete")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView doForensicsDeleteDetection(@PathVariable String orgCode, @PathVariable String detCode, ModelMap modelMap) throws Exception {
		listDetectionsForensicsService.doForensicsDeleteDetection(orgCode, detCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/detections?deleted");
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections/{detCode}/marketplace/add")
	public ModelAndView doAddMyMarketplaceDetection(@PathVariable String orgCode, @PathVariable String detCode, ModelMap modelMap) throws Exception {
		listDetectionsForensicsService.doAddMyMarketplaceDetection(orgCode, detCode, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/detections?marketplaceAdded");
	}
}