package com.exathreat.organisation.settings.processors;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationProcessor;
import com.exathreat.common.jpa.repository.OrganisationProcessorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditProcessorsSettingsService {

	@Autowired
	private OrganisationProcessorRepository organisationProcessorRepository;
	
	@Transactional(readOnly = true)
	public void initEditProcessors(String orgCode, EditProcessorsSettingsForm editProcessorsSettingsForm, ModelMap modelMap) throws Exception {
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		editProcessorsSettingsForm.setOrganisationProcessors(organisationProcessorRepository.findByOrganisationOrderByProcessorAcronymAsc(currentOrganisation));
	}

	@Transactional(readOnly = false)
	public void doEditProcessors(String orgCode, EditProcessorsSettingsForm editProcessorsSettingsForm, ModelMap modelMap) throws Exception {
		for (OrganisationProcessor organisationProcessorDto : editProcessorsSettingsForm.getOrganisationProcessors()) {
			OrganisationProcessor organisationProcessor = organisationProcessorRepository.findByProcCode(organisationProcessorDto.getProcCode());
			if (organisationProcessorDto.getEnabled() != organisationProcessor.getEnabled()) {
				organisationProcessor.setEnabled(organisationProcessorDto.getEnabled());
				organisationProcessor.setModified(ZonedDateTime.now(ZoneOffset.UTC));

				organisationProcessorRepository.saveAndFlush(organisationProcessor);
			}
		}
	}
}