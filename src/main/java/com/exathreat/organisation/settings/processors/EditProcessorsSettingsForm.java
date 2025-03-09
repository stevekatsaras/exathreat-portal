package com.exathreat.organisation.settings.processors;

import java.util.List;

import com.exathreat.common.jpa.entity.OrganisationProcessor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditProcessorsSettingsForm {
	private List<OrganisationProcessor> organisationProcessors;
}