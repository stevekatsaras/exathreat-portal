package com.exathreat.organisation.insights.explore;

import java.util.Map;

import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
public class ExploreInsightsController {

	@Autowired
	private AccessService accessService;

	@Autowired
	private ExploreInsightsService exploreInsightsService;

	@ModelAttribute
	public void getModelAttributes(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		accessService.getCurrentUserInfo(modelMap);
		accessService.getCurrentUserOrganisationInfo(orgCode, modelMap);
		exploreInsightsService.getInsightsMetadata(modelMap);
	}

	@GetMapping("/organisation/{orgCode}/insights/explore")
	public ModelAndView initInsightsExplore(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		return new ModelAndView("organisation/insights/explore/show");
	}

	@GetMapping(value = "/organisation/{orgCode}/insights/explore/item/list")
	public @ResponseBody Map<String, Object> listExploreItems(@PathVariable String orgCode, ModelMap modelMap) throws Exception {
		return exploreInsightsService.listExploreItems(orgCode, modelMap);
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/explore/item/open", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ExploreInsightItemData openExploreItem(@PathVariable String orgCode, @RequestBody ExploreInsightItemData exploreInsightItemData, ModelMap modelMap) throws Exception {
		return exploreInsightsService.openExploreItem(orgCode, exploreInsightItemData, modelMap);
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/explore/item/save", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ExploreInsightItemData saveExploreItem(@PathVariable String orgCode, @RequestBody ExploreInsightItemData exploreInsightItemData, ModelMap modelMap) throws Exception {
		return exploreInsightsService.saveExploreItem(orgCode, exploreInsightItemData, modelMap);
	}
	
	@PostMapping(value = "/organisation/{orgCode}/insights/explore/data/load", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ModelAndView loadExploreData(@PathVariable String orgCode, @RequestBody ExploreInsightsData exploreInsightsData, ModelMap modelMap) throws Exception {
		boolean loaded = exploreInsightsService.loadExploreData(orgCode, exploreInsightsData, modelMap);
		return new ModelAndView("organisation/insights/explore/fragments :: " + ((loaded) ? "exData" : "noExData"));
	}

	@PostMapping(value = "/organisation/{orgCode}/insights/explore/data/scroll", consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ModelAndView scrollExploreData(@PathVariable String orgCode, @RequestBody ExploreInsightsData exploreInsightsData, ModelMap modelMap) throws Exception {
		boolean scrolled = exploreInsightsService.scrollExploreData(orgCode, exploreInsightsData, modelMap);
		return new ModelAndView("organisation/insights/explore/fragments :: " + ((scrolled) ? "exDataScroll" : "noExDataScroll"));
	}
}