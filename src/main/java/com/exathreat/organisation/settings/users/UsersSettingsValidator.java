package com.exathreat.organisation.settings.users;

import com.exathreat.common.config.factory.DebounceFactory;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.enums.OrganisationUserRoleEnum;
import com.exathreat.common.jpa.repository.OrganisationUserRepository;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;

@Component
public class UsersSettingsValidator {
	
	@Autowired
	private DebounceFactory debounceFactory;

	@Autowired
	private OrganisationUserRepository organisationUserRepository;
	
	public void validateUserEmailAddress(String orgCode, AddUsersSettingsForm addUsersSettingsForm, ModelMap modelMap, Errors errors) throws Exception {
		if (!errors.hasFieldErrors("organisationUser.emailAddress")) {
			OrganisationUser organisationUserDto = addUsersSettingsForm.getOrganisationUser();

			Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
			OrganisationUser organisationUser = organisationUserRepository.findByEmailAddressAndOrganisation(organisationUserDto.getEmailAddress(), currentOrganisation);

			if (organisationUser != null) {
				errors.rejectValue("organisationUser.emailAddress", "invalid", "Email address already registered");
			}
			else if (!EmailValidator.getInstance().isValid(organisationUserDto.getEmailAddress())) {
				errors.rejectValue("organisationUser.emailAddress", "invalid", "Email address is invalid");
			}
			else if (debounceFactory.isDisposableEmailAddress(organisationUserDto.getEmailAddress())) {
				errors.rejectValue("organisationUser.emailAddress", "invalid", "Email address is invalid");
			}
		}
	}

	public void validateUserRole(String orgCode, String userCode, EditUsersSettingsForm editUsersSettingsForm, ModelMap modelMap, Errors errors) throws Exception {
		if (!errors.hasFieldErrors("organisationUser.userRole")) {
			OrganisationUser organisationUserDto = editUsersSettingsForm.getOrganisationUser();

			OrganisationUser organisationUser = organisationUserRepository.findByUserCode(userCode);
			if (organisationUser.getUserOwner() && !organisationUserDto.getUserRole().equals(OrganisationUserRoleEnum.ADMIN)) {
				errors.rejectValue("organisationUser.userRole", "invalid", "Owner cannot be demoted from ADMIN role");
			}
		}
	}
}