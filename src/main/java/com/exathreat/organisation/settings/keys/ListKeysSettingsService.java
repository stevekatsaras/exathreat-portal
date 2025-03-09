package com.exathreat.organisation.settings.keys;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationKey;
import com.exathreat.common.jpa.repository.OrganisationKeyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListKeysSettingsService {

	@Autowired
	private OrganisationKeyRepository organisationKeyRepository;
	
	@Transactional(readOnly = true)
	public void initKeys(String orgCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationKeys", organisationKeyRepository.findByOrganisationOrderByIdDesc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = false)
	public void doDisableKey(String orgCode, String keyCode, ModelMap modelMap) throws Exception {
		OrganisationKey organisationKey = organisationKeyRepository.findByKeyCode(keyCode);
		organisationKey.setEnabled(false);
		organisationKey.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationKey = organisationKeyRepository.saveAndFlush(organisationKey);
	}

	@Transactional(readOnly = false)
	public void doEnableKey(String orgCode, String keyCode, ModelMap modelMap) throws Exception {
		OrganisationKey organisationKey = organisationKeyRepository.findByKeyCode(keyCode);
		organisationKey.setEnabled(true);
		organisationKey.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationKey = organisationKeyRepository.saveAndFlush(organisationKey);
	}

	@Transactional(readOnly = false)
	public void doDeleteKey(String orgCode, String keyCode, ModelMap modelMap) throws Exception {
		OrganisationKey organisationKey = organisationKeyRepository.findByKeyCode(keyCode);
		organisationKeyRepository.delete(organisationKey);
	}
}