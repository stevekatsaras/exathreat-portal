package com.exathreat.organisation.settings.users;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.enums.OrganisationUserRoleEnum;
import com.exathreat.common.jpa.repository.OrganisationUserRepository;
import com.exathreat.common.service.AccessService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditUsersSettingsService {

	@Autowired
	private AccessService accessService;

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
	
	@Transactional(readOnly = true)
	public void initEditUser(String orgCode, String userCode, EditUsersSettingsForm editUsersSettingsForm, ModelMap modelMap) throws Exception {
		editUsersSettingsForm.setOrganisationUser(organisationUserRepository.findByUserCode(userCode));
	}

	@Transactional(readOnly = false)
	public void doEditUser(String orgCode, String userCode, EditUsersSettingsForm editUsersSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationUser organisationUserDto = editUsersSettingsForm.getOrganisationUser();

		OrganisationUser organisationUser = organisationUserRepository.findByUserCode(userCode);
		organisationUser.setGivenName(organisationUserDto.getGivenName());
		organisationUser.setSurname(organisationUserDto.getSurname());
		organisationUser.setTelephone(organisationUserDto.getTelephone());
		organisationUser.setMobile(organisationUserDto.getMobile());
		organisationUser.setUserRole(organisationUserDto.getUserRole());
		organisationUser.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationUserRepository.saveAndFlush(organisationUser);

		// if I have updated myself, refresh my access controls in my session...
		
		OrganisationUser loggedInUser = (OrganisationUser) modelMap.get("loggedInUser");
		if (userCode.equals(loggedInUser.getUserCode())) {
			accessService.refreshLoggedInUserRoleAccess(modelMap);
		}
	}
}