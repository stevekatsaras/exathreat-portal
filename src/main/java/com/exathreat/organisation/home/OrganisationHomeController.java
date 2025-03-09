package com.exathreat.organisation.home;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.enums.OrganisationStatusEnum;
import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class OrganisationHomeController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private OrganisationHomeService organisationHomeService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@GetMapping("/organisation/{orgCode}")
	public ModelAndView initHome(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		String viewPage = "organisation/home";

		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		if (OrganisationStatusEnum.Discontinued.name().equals(currentOrganisation.getStatus())) {
			viewPage = "redirect:/organisation/" + orgCode + "/activate";
		}
		return new ModelAndView(viewPage);
	}
	
	@GetMapping("/organisation/{orgCode}/activate")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN')")
	public ModelAndView initActivate(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		return new ModelAndView("organisation/activate");
	}

	@PostMapping("/organisation/{orgCode}/activate")
	public ModelAndView doActivate(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		organisationHomeService.doActivate(orgCode);
		return new ModelAndView("redirect:/organisation/" + orgCode + "?activated");
	}

}