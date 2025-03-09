package com.exathreat.organisation.insights.visualize;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.OrganisationVisualization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditVisualizeDashboardForm {
	@Valid
	private OrganisationVisualization organisationVisualization;	
}