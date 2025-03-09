package com.exathreat.organisation.insights.visualize;

import java.util.Map;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AddVisualizeChartController {

	@Autowired
	private AccessService accessService;
	
	@Autowired
	private AddVisualizeChartService addVisualizeChartService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		addVisualizeChartService.getChartMetadata(modelMap);
	}
	
	@GetMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add")
	public ModelAndView initInsightsAddChartVisualization(@PathVariable String orgCode, @PathVariable String vizCode, AddVisualizeChartForm addVisualizeChartForm, ModelMap modelMap) throws Exception {
		addVisualizeChartService.initInsightsAddChartVisualization(orgCode, vizCode, addVisualizeChartForm, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/add");
	}

	@PostMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add")
	public ModelAndView doInsightsAddChartVisualization(@PathVariable String orgCode, @PathVariable String vizCode, AddVisualizeChartForm addVisualizeChartForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		addVisualizeChartService.doInsightsAddChartVisualization(orgCode, vizCode, addVisualizeChartForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/insights/visualize/" + vizCode + "/run");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add/query/add")
	public ModelAndView addQuery(@PathVariable String orgCode, @PathVariable String vizCode, AddVisualizeChartForm addVisualizeChartForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		addVisualizeChartService.addQuery(orgCode, vizCode, addVisualizeChartForm, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/fragments :: addChartQueries");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add/query/clone")
	public ModelAndView cloneQuery(@PathVariable String orgCode, @PathVariable String vizCode, AddVisualizeChartForm addVisualizeChartForm, @RequestParam("queryIndex") Integer queryIndex, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		addVisualizeChartService.cloneQuery(orgCode, vizCode, addVisualizeChartForm, queryIndex, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/fragments :: addChartQueries");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add/query/remove")
	public ModelAndView removeQuery(@PathVariable String orgCode, @PathVariable String vizCode, AddVisualizeChartForm addVisualizeChartForm, @RequestParam("queryIndex") Integer queryIndex, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		addVisualizeChartService.removeQuery(orgCode, vizCode, addVisualizeChartForm, queryIndex, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/fragments :: addChartQueries");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add/field/mappings")
	public @ResponseBody Map<String, Object> getFieldMappings(@PathVariable String orgCode, @PathVariable String vizCode, @RequestBody Map<String, Object> params, ModelMap modelMap) throws Exception {
		return addVisualizeChartService.getFieldMappings(orgCode, vizCode, params, modelMap);
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/add/load/data")
	public @ResponseBody AddVisualizeChartForm loadData(@PathVariable String orgCode, @PathVariable String vizCode, AddVisualizeChartForm addVisualizeChartForm, ModelMap modelMap) throws Exception {
		return addVisualizeChartService.loadData(orgCode, vizCode, addVisualizeChartForm, modelMap);
	}

}