package com.exathreat.organisation.settings.general;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.BusinessType;
import com.exathreat.common.jpa.entity.Country;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.enums.OrganisationStatusEnum;
import com.exathreat.common.jpa.repository.BusinessTypeRepository;
import com.exathreat.common.jpa.repository.CountryRepository;
import com.exathreat.common.jpa.repository.OrganisationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditOrganisationSettingsService {

	@Autowired
	private BusinessTypeRepository businessTypeRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private OrganisationRepository organisationRepository;
	
	@Transactional(readOnly = true)
	public void getOrganisationMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("businessTypes", businessTypeRepository.findAllByOrderByNameAsc());
		modelMap.addAttribute("countries", countryRepository.findAllByOrderByNameAsc());
	}
	
	@Transactional(readOnly = true)
	public void initEditGeneralSettings(String orgCode, EditOrganisationSettingsForm editOrganisationSettingsForm, ModelMap modelMap) throws Exception {
		editOrganisationSettingsForm.setOrganisation((Organisation) modelMap.get("currentOrganisation"));
	}

	@Transactional(readOnly = false)
	public void doEditGeneralSettings(String orgCode, EditOrganisationSettingsForm editOrganisationSettingsForm, ModelMap modelMap) throws Exception {
		Organisation organisationDto = editOrganisationSettingsForm.getOrganisation();

		BusinessType businessType = businessTypeRepository.getOne(organisationDto.getBusinessType().getId());
		Country country = countryRepository.getOne(organisationDto.getCountry().getId());

		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		currentOrganisation.setOrgName(organisationDto.getOrgName());
		currentOrganisation.setBusinessType(businessType);
		currentOrganisation.setBusinessNumber(organisationDto.getBusinessNumber());
		currentOrganisation.setWebsite(organisationDto.getWebsite());
		currentOrganisation.setAddress1(organisationDto.getAddress1());
		currentOrganisation.setAddress2(organisationDto.getAddress2());
		currentOrganisation.setCity(organisationDto.getCity());
		currentOrganisation.setState(organisationDto.getState());
		currentOrganisation.setPostcode(organisationDto.getPostcode());
		currentOrganisation.setCountry(country);
		currentOrganisation.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		currentOrganisation = organisationRepository.saveAndFlush(currentOrganisation);
	}

	@Transactional(readOnly = false)
	public void doDeactivate(String orgCode, ModelMap modelMap) throws Exception {
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		currentOrganisation.setStatus(OrganisationStatusEnum.Discontinued.name());
		currentOrganisation.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		currentOrganisation = organisationRepository.saveAndFlush(currentOrganisation);
	}

}