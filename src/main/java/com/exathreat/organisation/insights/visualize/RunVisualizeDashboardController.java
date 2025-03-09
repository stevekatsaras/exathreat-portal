package com.exathreat.organisation.insights.visualize;

import java.util.Map;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunVisualizeDashboardController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private RunVisualizeDashboardService runVisualizeDashboardService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
	}

	@PostMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/run/save")
	public void save(@PathVariable String orgCode, @PathVariable String vizCode, @RequestBody Map<String, Object> params, ModelMap modelMap) throws Exception {
		runVisualizeDashboardService.save(orgCode, vizCode, params, modelMap);
	}
	
	@PostMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/run/chart/load/data")
	public @ResponseBody RunVisualizeDashboardChartData loadData(@PathVariable String orgCode, @PathVariable String vizCode, @RequestBody RunVisualizeDashboardChartData runVisualizeDashboardChartData, ModelMap modelMap) throws Exception {
		return runVisualizeDashboardService.loadData(orgCode, vizCode, runVisualizeDashboardChartData, modelMap);
	}

	@GetMapping("/organisation/{orgCode}/insights/visualize/{vizCode}/run/chart/delete")
	public void deleteChart(@PathVariable String orgCode, @PathVariable String vizCode, @RequestParam String chartCode, ModelMap modelMap) throws Exception {
		runVisualizeDashboardService.deleteChart(orgCode, vizCode, chartCode, modelMap);
	}
}