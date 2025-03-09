package com.exathreat.organisation.settings.keys;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.OrganisationKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditKeysSettingsForm {
	@Valid
	private OrganisationKey organisationKey;
}