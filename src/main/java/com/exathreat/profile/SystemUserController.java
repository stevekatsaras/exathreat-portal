package com.exathreat.profile;

import javax.validation.Valid;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SystemUserController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private SystemUserService systemUserService;

	@ModelAttribute
	public void getModelAttributes(ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
	}

 	@GetMapping("/system")
	public ModelAndView initSystem() throws Exception {
		return new ModelAndView("redirect:/system/user");
	}

	@GetMapping("/system/user")
	public ModelAndView initSystemUser(SystemUserForm systemUserForm, ModelMap modelMap) throws Exception {
		systemUserService.initSystemUser(systemUserForm, modelMap);
		return new ModelAndView("system/user");
	}

	@PostMapping("/system/user")
	public ModelAndView doSystemUser(@Valid SystemUserForm systemUserForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		if (bindingResult.hasErrors()) {
			return new ModelAndView("system/user");
		}
		systemUserService.doSystemUser(systemUserForm, modelMap);
		return new ModelAndView("redirect:/system/user?updated");
	}

}