package com.exathreat.organisation.insights.visualize;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationVisualization;
import com.exathreat.common.jpa.repository.OrganisationVisualizationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListVisualizeInsightsService {

	@Autowired
	private OrganisationVisualizationRepository organisationVisualizationRepository;

	@Transactional(readOnly = true)
	public void initInsightsVisualize(String orgCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationVisualizations", organisationVisualizationRepository.findByOrganisationOrderByNameAsc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = true)
	public void initInsightsRunVisualization(String orgCode, String vizCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationVisualization", organisationVisualizationRepository.findByVizCode(vizCode));
	}

	@Transactional(readOnly = false)
	public void doInsightsDeleteVisualization(String orgCode, String vizCode, ModelMap modelMap) throws Exception {
		OrganisationVisualization organisationVisualization = organisationVisualizationRepository.findByVizCode(vizCode);
		organisationVisualizationRepository.delete(organisationVisualization);
	}
}