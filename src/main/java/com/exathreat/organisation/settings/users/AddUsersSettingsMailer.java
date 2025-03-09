package com.exathreat.organisation.settings.users;

import java.util.Map;

import com.exathreat.common.config.factory.ApplicationSettings;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.support.MailSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

@Component
public class AddUsersSettingsMailer {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private MailSupport mailSupport;
	
	@Async
	public void sendUserInvitation(OrganisationUser organisationUser, ModelMap modelMap) throws Exception {
		Map<String, Object> variables = Map.of(
			"organisationUser", organisationUser, 
			"invitedBy", (String) modelMap.get("fullName"), 
			"portalUrl", applicationSettings.getRootUri() + "/", 
			"logo", "logo"
		);
		
		mailSupport.sendEmail(organisationUser.getEmailAddress(), "Invitation to join Exathreat", "organisation/settings/users/emails/added", variables);
	}
}