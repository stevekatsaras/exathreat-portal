package com.exathreat.organisation.settings.indexes;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.OrganisationIndex;
import com.exathreat.common.jpa.entity.OrganisationInvoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditIndexSettingsForm {

	@Valid
	private OrganisationIndex organisationIndex;
	private OrganisationInvoice organisationInvoice;
}