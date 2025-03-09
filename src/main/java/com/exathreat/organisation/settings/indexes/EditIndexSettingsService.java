package com.exathreat.organisation.settings.indexes;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationIndex;
import com.exathreat.common.jpa.entity.enums.OrganisationInvoiceStatusEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationSubscriptionStatusEnum;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;
import com.exathreat.common.jpa.repository.OrganisationInvoiceRepository;
import com.exathreat.common.jpa.repository.OrganisationSubscriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditIndexSettingsService {

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	@Autowired
	private OrganisationInvoiceRepository organisationInvoiceRepository;

	@Autowired
	private OrganisationSubscriptionRepository organisationSubscriptionRepository;

	@Transactional(readOnly = true)
	public void initEditIndex(String orgCode, String indCode, EditIndexSettingsForm editIndexSettingsForm, ModelMap modelMap) throws Exception {
		editIndexSettingsForm.setOrganisationIndex(organisationIndexRepository.findByIndCode(indCode));
		editIndexSettingsForm.setOrganisationInvoice(organisationInvoiceRepository.findByOrganisationSubscriptionAndStatus(
			organisationSubscriptionRepository.findByOrganisationAndStatus((Organisation) modelMap.get("currentOrganisation"), OrganisationSubscriptionStatusEnum.Active.name()), 
			OrganisationInvoiceStatusEnum.New.name()));
	}

	@Transactional(readOnly = false)
	public void doEditIndex(String orgCode, String indCode, EditIndexSettingsForm editIndexSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationIndex organisationIndexDto = editIndexSettingsForm.getOrganisationIndex();

		OrganisationIndex organisationIndex = organisationIndexRepository.findByIndCode(indCode);
		organisationIndex.setRetentionDays(organisationIndexDto.getRetentionDays());
		organisationIndex.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationIndexRepository.saveAndFlush(organisationIndex);
	}
}