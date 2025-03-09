package com.exathreat.organisation.settings.users;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.repository.OrganisationUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListUsersSettingsService {

	@Autowired
	private ListUsersSettingsMailer listUsersSettingsMailer;

	@Autowired
	private OrganisationUserRepository organisationUserRepository;
	
	@Transactional(readOnly = true)
	public void initUsers(String orgCode, ModelMap modelMap) throws Exception {
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");

		modelMap.addAttribute("loggedInUser", organisationUserRepository.findByEmailAddressAndOrganisation((String) modelMap.get("emailAddress"), currentOrganisation));
		modelMap.addAttribute("organisationUsers", organisationUserRepository.findByOrganisationOrderByEmailAddressAsc(currentOrganisation));
	}

	@Transactional(readOnly = false)
	public void doRemoveUser(String orgCode, String userCode, ModelMap modelMap) throws Exception {
		organisationUserRepository.delete(organisationUserRepository.findByUserCode(userCode));
	}

	@Transactional(readOnly = false)
	public void resendUserInvitation(String orgCode, String userCode, ModelMap modelMap) throws Exception {
		OrganisationUser organisationUser = organisationUserRepository.findByUserCode(userCode);
		listUsersSettingsMailer.resendUserInvitation(organisationUser, modelMap);
	}
}