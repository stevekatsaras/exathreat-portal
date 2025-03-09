package com.exathreat.organisation.settings.keys;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.OrganisationKey;
import com.exathreat.common.jpa.repository.OrganisationKeyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditKeysSettingsService {

	@Autowired
	private OrganisationKeyRepository organisationKeyRepository;

	@Transactional(readOnly = true)
	public void initEditKey(String orgCode, String keyCode, EditKeysSettingsForm editKeysSettingsForm, ModelMap modelMap) throws Exception {
		editKeysSettingsForm.setOrganisationKey(organisationKeyRepository.findByKeyCode(keyCode));
	}

	@Transactional(readOnly = false)
	public void doEditKey(String orgCode, String keyCode, EditKeysSettingsForm editKeysSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationKey organisationKeyDto = editKeysSettingsForm.getOrganisationKey();
		
		OrganisationKey organisationKey = organisationKeyRepository.findByKeyCode(keyCode);
		organisationKey.setName(organisationKeyDto.getName());
		organisationKey.setDescription(organisationKeyDto.getDescription());
		organisationKey.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationKeyRepository.saveAndFlush(organisationKey);
	}

}