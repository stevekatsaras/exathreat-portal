package com.exathreat.organisation.settings.users;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.OrganisationUser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditUsersSettingsForm {
	@Valid
	private OrganisationUser organisationUser;
}