package com.exathreat.organisation.settings.invoices;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.repository.OrganisationInvoiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListInvoiceSettingsService {

	@Autowired
	private OrganisationInvoiceRepository organisationInvoiceRepository;

	@Transactional(readOnly = true)
	public void initInvoices(String orgCode, ModelMap modelMap) throws Exception {
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		modelMap.addAttribute("organisationInvoices", organisationInvoiceRepository.findByOrganisationSubscriptionOrganisationOrderByPeriodFromDesc(currentOrganisation));
	}
}