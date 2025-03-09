package com.exathreat.organisation.create;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationSubscription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class CreateOrganisationForm {
	@Valid
	private Organisation organisation;

	@Valid
	private OrganisationSubscription organisationSubscription;
}