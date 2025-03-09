package com.exathreat.organisation.settings.notifications;

import javax.validation.Valid;

import com.exathreat.common.jpa.entity.OrganisationNotification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder @EqualsAndHashCode @Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class EditNotificationsSettingsForm {
	private String action;
	
	@Valid
	private OrganisationNotification organisationNotification;
}