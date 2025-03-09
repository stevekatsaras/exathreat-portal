package com.exathreat.organisation.settings.subscriptions;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationSubscription;
import com.exathreat.common.jpa.entity.enums.OrganisationSubscriptionStatusEnum;
import com.exathreat.common.jpa.repository.OrganisationSubscriptionRepository;
import com.exathreat.common.jpa.repository.SubscriptionRepository;
import com.exathreat.common.support.JsonSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ChangeSubscriptionSettingsService {

	@Autowired
	private JsonSupport jsonSupport;

	@Autowired
	private OrganisationSubscriptionRepository organisationSubscriptionRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Transactional(readOnly = true)
	public void getSubscriptionMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("subscriptions", subscriptionRepository.findByEnabledOrderByPriceAmountAsc(true));
	}

	@Transactional(readOnly = true)
	public void initChangeSubscription(String orgCode, ChangeSubscriptionSettingsForm changeSubscriptionSettingsForm, ModelMap modelMap) throws Exception {
		Integer selectedSubscriptionId = 0;

		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		OrganisationSubscription organisationSubscription = organisationSubscriptionRepository.findByOrganisationAndStatus(currentOrganisation, OrganisationSubscriptionStatusEnum.New.name());
		if (organisationSubscription != null) {
			selectedSubscriptionId = (Integer) organisationSubscription.getSubscription().get("id");
		}
		else {
			organisationSubscription = organisationSubscriptionRepository.findByOrganisationAndStatus(currentOrganisation, OrganisationSubscriptionStatusEnum.Active.name());
			selectedSubscriptionId = (Integer) organisationSubscription.getSubscription().get("id");
		}
		
		changeSubscriptionSettingsForm.setOrganisationSubscription(OrganisationSubscription.builder()
			.subscription(new HashMap<String, Object>(Map.of("id", selectedSubscriptionId)))
			.build());
	}

	@Transactional(readOnly = false)
	public String doChangeSubscription(String orgCode, ChangeSubscriptionSettingsForm changeSubscriptionSettingsForm, ModelMap modelMap) throws Exception {
		String outcome = "";

		OrganisationSubscription organisationSubscriptionDto = changeSubscriptionSettingsForm.getOrganisationSubscription();
		Long selectedSubscriptionId = Long.parseLong((String) organisationSubscriptionDto.getSubscription().get("id"));	// incoming from the form

		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		OrganisationSubscription organisationSubscriptionNew = organisationSubscriptionRepository.findByOrganisationAndStatus(currentOrganisation, OrganisationSubscriptionStatusEnum.New.name());
		OrganisationSubscription organisationSubscriptionActive = organisationSubscriptionRepository.findByOrganisationAndStatus(currentOrganisation, OrganisationSubscriptionStatusEnum.Active.name());

		if (organisationSubscriptionNew != null) {
			if (selectedSubscriptionId == Long.valueOf(((Integer) organisationSubscriptionNew.getSubscription().get("id")).longValue())) {
				outcome = "void";
			}
			else if (selectedSubscriptionId == Long.valueOf(((Integer) organisationSubscriptionActive.getSubscription().get("id")).longValue())) {
				organisationSubscriptionRepository.delete(organisationSubscriptionNew);
				outcome = "aborted";
			}
			else {
				organisationSubscriptionNew.setSubscription(jsonSupport.toHashMap(subscriptionRepository.getOne(selectedSubscriptionId)));
				organisationSubscriptionNew.setModified(ZonedDateTime.now(ZoneOffset.UTC));

				organisationSubscriptionRepository.saveAndFlush(organisationSubscriptionNew);
				outcome = "changed";
			}
		}
		else if (selectedSubscriptionId != Long.valueOf(((Integer) organisationSubscriptionActive.getSubscription().get("id")).longValue())) {
			OrganisationSubscription organisationSubscription = OrganisationSubscription.builder()
				.subCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
				.status(OrganisationSubscriptionStatusEnum.New.name())
				.subscription(jsonSupport.toHashMap(subscriptionRepository.getOne(selectedSubscriptionId)))
				.created(ZonedDateTime.now(ZoneOffset.UTC))
				.modified(ZonedDateTime.now(ZoneOffset.UTC))
				.organisation(currentOrganisation)
				.build();
			
			organisationSubscriptionRepository.saveAndFlush(organisationSubscription);
			outcome = "changed";
		}
		else {
			outcome = "void";
		}
		return outcome;
	}
}