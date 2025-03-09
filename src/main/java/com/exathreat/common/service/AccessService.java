package com.exathreat.common.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.MarketplaceUser;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.SystemUser;
import com.exathreat.common.jpa.repository.MarketplaceUserRepository;
import com.exathreat.common.jpa.repository.OrganisationRepository;
import com.exathreat.common.jpa.repository.OrganisationUserRepository;
import com.exathreat.common.jpa.repository.SystemUserRepository;

import org.elasticsearch.common.collect.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class AccessService {

	@Autowired
	private MarketplaceUserRepository marketplaceUserRepository;

	@Autowired
	private OrganisationRepository organisationRepository;
	
	@Autowired
	private OrganisationUserRepository organisationUserRepository;

	@Autowired
	private SystemUserRepository systemUserRepository;
	
	@Transactional(readOnly = false)
	public void getCurrentUserInfo(ModelMap modelMap) throws Exception {
		OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		modelMap.addAttribute("emailAddress", oidcUser.getEmail());
		modelMap.addAttribute("givenName", oidcUser.getGivenName());
		modelMap.addAttribute("surname", oidcUser.getFamilyName());
		modelMap.addAttribute("fullName", oidcUser.getAttributes().get("name").toString());
		modelMap.addAttribute("userOrganisations", organisationUserRepository.findByEmailAddressOrderByOrganisationOrgNameAsc(oidcUser.getEmail()));
		
		SystemUser systemUser = systemUserRepository.findByEmailAddress(oidcUser.getEmail());
		if (systemUser == null) {
			systemUser = SystemUser.builder()
				.emailAddress(oidcUser.getEmail())
				.dateFormat("EEE, MMM dd yyyy|ddd, MMM DD YYYY|D, M d Y")
				.timeFormat("HH:mm:ss|HH:mm:ss|H:i:s")
				.timezone("UTC")
				.created(ZonedDateTime.now(ZoneOffset.UTC))
				.modified(ZonedDateTime.now(ZoneOffset.UTC))
				.build();
			
			systemUserRepository.saveAndFlush(systemUser);
		}
		modelMap.addAttribute("javaDateFormat", systemUser.getDateFormat().split("\\|")[0]);
		modelMap.addAttribute("javaTimeFormat", systemUser.getTimeFormat().split("\\|")[0]);
		modelMap.addAttribute("jsDateFormat", systemUser.getDateFormat().split("\\|")[1]);
		modelMap.addAttribute("jsTimeFormat", systemUser.getTimeFormat().split("\\|")[1]);
		modelMap.addAttribute("phpDateFormat", systemUser.getDateFormat().split("\\|")[2]);
		modelMap.addAttribute("phpTimeFormat", systemUser.getTimeFormat().split("\\|")[2]);
		modelMap.addAttribute("timezone", systemUser.getTimezone());

		MarketplaceUser marketplaceUser = marketplaceUserRepository.findByEmailAddress(oidcUser.getEmail());
		if (marketplaceUser != null) {
			modelMap.addAttribute("marketplaceUser", marketplaceUser);
		}
	}

	@Transactional(readOnly = true)
	public void getCurrentUserOrganisationInfo(String orgCode, ModelMap modelMap) throws Exception {
		String emailAddress = (String) modelMap.get("emailAddress");
		Organisation currentOrganisation = organisationRepository.findByOrgCode(orgCode);
		OrganisationUser loggedInUser = organisationUserRepository.findByEmailAddressAndOrganisation(emailAddress, currentOrganisation);

		OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		SecurityContextHolder.getContext().setAuthentication(new OAuth2AuthenticationToken(
			(OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
			List.of(new SimpleGrantedAuthority("ROLE_" + loggedInUser.getUserRole())),
			oidcUser.getName()
		));

		modelMap.addAttribute("currentOrganisation", currentOrganisation);
		modelMap.addAttribute("loggedInUser", loggedInUser);
	}

	public void refreshLoggedInUserRoleAccess(ModelMap modelMap) {
		OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		OrganisationUser loggedInUser = (OrganisationUser) modelMap.get("loggedInUser");

		SecurityContextHolder.getContext().setAuthentication(new OAuth2AuthenticationToken(
			(OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
			List.of(new SimpleGrantedAuthority("ROLE_" + loggedInUser.getUserRole())),
			oidcUser.getName()
		));
	}
}