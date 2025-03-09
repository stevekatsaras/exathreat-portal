package com.exathreat.organisation.insights.visualize;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.OrganisationVisualization;
import com.exathreat.common.jpa.repository.OrganisationVisualizationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class AddVisualizeDashboardService {

	@Autowired
	private OrganisationVisualizationRepository organisationVisualizationRepository;

	public void initInsightsAddVisualization(String orgCode, AddVisualizeDashboardForm addVisualizeDashboardForm) throws Exception {
		addVisualizeDashboardForm.setOrganisationVisualization(OrganisationVisualization.builder().build());
	}

	@Transactional(readOnly = false)
	public void doInsightsAddVisualization(String orgCode, AddVisualizeDashboardForm addVisualizeDashboardForm, ModelMap modelMap) throws Exception {
		OrganisationVisualization organisationVisualizationDto = addVisualizeDashboardForm.getOrganisationVisualization();
		OrganisationVisualization organisationVisualization = OrganisationVisualization.builder()
			.vizCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.name(organisationVisualizationDto.getName())
			.description(organisationVisualizationDto.getDescription())
			.dateRange("Last 1 hour")
			.dateFormat((String) modelMap.get("javaDateFormat"))
			.timeFormat((String) modelMap.get("javaTimeFormat"))
			.refresh("Off")
			.charts(Map.of("panels", List.of()))
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisationUser((OrganisationUser) modelMap.get("loggedInUser"))
			.organisation((Organisation) modelMap.get("currentOrganisation"))
			.build();
		
		organisationVisualizationRepository.saveAndFlush(organisationVisualization);
	}
}