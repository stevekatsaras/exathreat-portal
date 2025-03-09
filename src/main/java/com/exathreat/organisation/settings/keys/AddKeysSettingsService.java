package com.exathreat.organisation.settings.keys;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationKey;
import com.exathreat.common.jpa.entity.enums.OrganisationKeyTypeEnum;
import com.exathreat.common.jpa.repository.OrganisationKeyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class AddKeysSettingsService {

	@Autowired
	private OrganisationKeyRepository organisationKeyRepository;

	@Transactional(readOnly = false)
	public void doAddKey(String orgCode, AddKeysSettingsForm addKeysSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationKey organisationKeyDto = addKeysSettingsForm.getOrganisationKey();

		OrganisationKey organisationKey = OrganisationKey.builder()
			.keyCode(UUID.randomUUID().toString())
			.name(organisationKeyDto.getName())
			.description(organisationKeyDto.getDescription())
			.keyType(OrganisationKeyTypeEnum.api.name())
			.enabled(true)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation((Organisation) modelMap.get("currentOrganisation"))
			.build();

		organisationKey = organisationKeyRepository.saveAndFlush(organisationKey);
	}
}