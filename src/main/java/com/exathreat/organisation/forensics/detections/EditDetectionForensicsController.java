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
public class EditDetectionForensicsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private EditDetectionForensicsService editDetectionForensicsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		editDetectionForensicsService.getDetectionMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/forensics/detections/{detCode}/edit")
	public ModelAndView initForensicsEditDetection(@PathVariable String orgCode, @PathVariable String detCode, EditDetectionForensicsForm editDetectionForensicsForm, ModelMap modelMap) throws Exception {
		editDetectionForensicsService.initForensicsEditDetection(orgCode, detCode, editDetectionForensicsForm, modelMap);
		return new ModelAndView("organisation/forensics/detections/edit");
	}

	@PostMapping("/organisation/{orgCode}/forensics/detections/{detCode}/edit")
	public ModelAndView doForensicsEditDetection(@PathVariable String orgCode, @PathVariable String detCode, @Valid EditDetectionForensicsForm editDetectionForensicsForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("organisation/forensics/detections/edit");
		}
		editDetectionForensicsService.doForensicsEditDetection(orgCode, detCode, editDetectionForensicsForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/forensics/detections?updated");
	}
	
}