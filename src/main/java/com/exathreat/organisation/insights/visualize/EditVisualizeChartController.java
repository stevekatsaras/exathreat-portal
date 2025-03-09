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
public class EditVisualizeChartController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private EditVisualizeChartService editVisualizeChartService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		editVisualizeChartService.getChartMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit")
	public ModelAndView initInsightsEditChartVisualization(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, EditVisualizeChartForm editVisualizeChartForm, ModelMap modelMap) throws Exception {
		editVisualizeChartService.initInsightsEditChartVisualization(orgCode, vizCode, chartCode, editVisualizeChartForm, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/edit");
	}
	
	@PostMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit")
	public ModelAndView doInsightsEditChartVisualization(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, EditVisualizeChartForm editVisualizeChartForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		editVisualizeChartService.doInsightsEditChartVisualization(orgCode, vizCode, chartCode, editVisualizeChartForm, modelMap);
		return new ModelAndView("redirect:/organisation/" + orgCode + "/insights/visualize/" + vizCode + "/run");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit/query/add")
	public ModelAndView addQuery(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, EditVisualizeChartForm editVisualizeChartForm, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		editVisualizeChartService.addQuery(orgCode, vizCode, chartCode, editVisualizeChartForm, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/fragments :: editChartQueries");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit/query/clone")
	public ModelAndView cloneQuery(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, EditVisualizeChartForm editVisualizeChartForm, @RequestParam("queryIndex") Integer queryIndex, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		editVisualizeChartService.cloneQuery(orgCode, vizCode, chartCode, editVisualizeChartForm, queryIndex, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/fragments :: editChartQueries");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit/query/remove")
	public ModelAndView removeQuery(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, EditVisualizeChartForm editVisualizeChartForm, @RequestParam("queryIndex") Integer queryIndex, BindingResult bindingResult, ModelMap modelMap) throws Exception {
		editVisualizeChartService.removeQuery(orgCode, vizCode, chartCode, editVisualizeChartForm, queryIndex, modelMap);
		return new ModelAndView("organisation/insights/visualize/chart/fragments :: editChartQueries");
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit/field/mappings")
	public @ResponseBody Map<String, Object> getFieldMappings(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, @RequestBody Map<String, Object> params, ModelMap modelMap) throws Exception {
		return editVisualizeChartService.getFieldMappings(orgCode, vizCode, chartCode, params, modelMap);
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/visualize/{vizCode}/chart/{chartCode}/edit/load/data")
	public @ResponseBody EditVisualizeChartForm loadData(@PathVariable String orgCode, @PathVariable String vizCode, @PathVariable String chartCode, EditVisualizeChartForm editVisualizeChartForm, ModelMap modelMap) throws Exception {
		return editVisualizeChartService.loadData(orgCode, vizCode, chartCode, editVisualizeChartForm, modelMap);
	}

}