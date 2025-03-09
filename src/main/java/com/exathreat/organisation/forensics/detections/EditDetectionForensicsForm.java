package com.exathreat.organisation.forensics.detections;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.OrganisationDetection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditDetectionForensicsForm {
	@Valid
	private OrganisationDetection organisationDetection;
}