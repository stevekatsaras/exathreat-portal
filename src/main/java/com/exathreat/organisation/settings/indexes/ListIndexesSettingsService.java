package com.exathreat.organisation.settings.indexes;

import com.exathreat.common.jpa.entity.Organisation;
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
public class ListIndexesSettingsService {

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	@Autowired
	private OrganisationInvoiceRepository organisationInvoiceRepository;

	@Autowired
	private OrganisationSubscriptionRepository organisationSubscriptionRepository;
	
	@Transactional(readOnly = true)
	public void initIndexes(String orgCode, ModelMap modelMap) throws Exception {
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		modelMap.addAttribute("organisationIndexes", organisationIndexRepository.findByOrganisationOrderByAliasNameDescCreatedDesc(currentOrganisation));
		modelMap.addAttribute("organisationInvoice", organisationInvoiceRepository.findByOrganisationSubscriptionAndStatus(
				organisationSubscriptionRepository.findByOrganisationAndStatus((Organisation) modelMap.get("currentOrganisation"), OrganisationSubscriptionStatusEnum.Active.name()), 
				OrganisationInvoiceStatusEnum.New.name()));
	}
	
}