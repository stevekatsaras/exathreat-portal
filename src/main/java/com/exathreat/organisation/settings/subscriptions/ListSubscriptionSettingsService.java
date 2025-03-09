package com.exathreat.organisation.settings.subscriptions;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.repository.OrganisationSubscriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListSubscriptionSettingsService {

	@Autowired
	private OrganisationSubscriptionRepository organisationSubscriptionRepository;

	@Transactional(readOnly = true)
	public void initSubscriptions(String orgCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationSubscriptions", organisationSubscriptionRepository.findByOrganisationOrderByIdDesc((Organisation) modelMap.get("currentOrganisation")));
	}
}