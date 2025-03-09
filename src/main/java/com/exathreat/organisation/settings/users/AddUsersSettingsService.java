package com.exathreat.organisation.settings.users;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.enums.OrganisationUserRoleEnum;
import com.exathreat.common.jpa.repository.OrganisationUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class AddUsersSettingsService {

	@Autowired
	private AddUsersSettingsMailer addUsersSettingsMailer;

	@Autowired
	private OrganisationUserRepository organisationUserRepository;

	public void getUserMetadata(ModelMap modelMap) throws Exception {
		OrganisationUser loggedInUser = (OrganisationUser) modelMap.get("loggedInUser");

		List<String> orgUserRoles = new ArrayList<String>(); 
		switch (loggedInUser.getUserRole()) {
			case "USER": 
				orgUserRoles.add(OrganisationUserRoleEnum.USER.name());
				orgUserRoles.add(OrganisationUserRoleEnum.VIEWER.name());
				break;
			default: 
				orgUserRoles.add(OrganisationUserRoleEnum.ADMIN.name());
				orgUserRoles.add(OrganisationUserRoleEnum.USER.name());
				orgUserRoles.add(OrganisationUserRoleEnum.VIEWER.name());
		}
		modelMap.addAttribute("orgUserRoles", orgUserRoles);
	}

	public void initAddUser(String orgCode, AddUsersSettingsForm addUsersSettingsForm, ModelMap modelMap) throws Exception {
		addUsersSettingsForm.setOrganisationUser(OrganisationUser.builder().userRole(OrganisationUserRoleEnum.USER.name()).build());
	}

	@Transactional(readOnly = false)
	public void doAddUser(String orgCode, AddUsersSettingsForm addUsersSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationUser organisationUserDto = addUsersSettingsForm.getOrganisationUser();
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
			
		OrganisationUser organisationUser = OrganisationUser.builder()
			.userCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.emailAddress(organisationUserDto.getEmailAddress())
			.givenName(organisationUserDto.getGivenName())
			.surname(organisationUserDto.getSurname())
			.telephone(organisationUserDto.getTelephone())
			.mobile(organisationUserDto.getMobile())
			.userOwner(false)
			.userRole(organisationUserDto.getUserRole())
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(currentOrganisation)
			.build();

		organisationUser = organisationUserRepository.saveAndFlush(organisationUser);
		addUsersSettingsMailer.sendUserInvitation(organisationUser, modelMap);
	}

}