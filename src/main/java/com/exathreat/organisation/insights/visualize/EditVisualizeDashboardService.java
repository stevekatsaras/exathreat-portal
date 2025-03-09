package com.exathreat.organisation.insights.visualize;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.OrganisationVisualization;
import com.exathreat.common.jpa.repository.OrganisationVisualizationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditVisualizeDashboardService {

	@Autowired
	private OrganisationVisualizationRepository organisationVisualizationRepository;

	@Transactional(readOnly = true)
	public void initInsightsEditVisualization(String orgCode, String vizCode, EditVisualizeDashboardForm editVisualizeDashboardForm) throws Exception {
		editVisualizeDashboardForm.setOrganisationVisualization(organisationVisualizationRepository.findByVizCode(vizCode));
	}

	@Transactional(readOnly = false)
	public void doInsightsEditVisualization(String orgCode, String vizCode, EditVisualizeDashboardForm editVisualizeDashboardForm, ModelMap modelMap) throws Exception {
		OrganisationVisualization organisationVisualizationDto = editVisualizeDashboardForm.getOrganisationVisualization();

		OrganisationVisualization organisationVisualization = organisationVisualizationRepository.findByVizCode(vizCode);
		organisationVisualization.setName(organisationVisualizationDto.getName());
		organisationVisualization.setDescription(organisationVisualizationDto.getDescription());
		organisationVisualization.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationVisualizationRepository.saveAndFlush(organisationVisualization);
	}
}