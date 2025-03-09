package com.exathreat.organisation.home;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationInvoice;
import com.exathreat.common.jpa.entity.OrganisationSubscription;
import com.exathreat.common.jpa.entity.enums.OrganisationInvoiceStatusEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationStatusEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationSubscriptionStatusEnum;
import com.exathreat.common.jpa.entity.enums.SubscriptionPricePeriodEnum;
import com.exathreat.common.jpa.repository.OrganisationInvoiceRepository;
import com.exathreat.common.jpa.repository.OrganisationRepository;
import com.exathreat.common.jpa.repository.OrganisationSubscriptionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganisationHomeService {

	@Autowired
	private OrganisationRepository organisationRepository;

	@Autowired
	private OrganisationInvoiceRepository organisationInvoiceRepository;

	@Autowired
	private OrganisationSubscriptionRepository organisationSubscriptionRepository;
	
	@Transactional(readOnly = false)
	public void doActivate(String orgCode) throws Exception {
		Organisation organisation = organisationRepository.findByOrgCode(orgCode);
		organisation.setStatus(OrganisationStatusEnum.Active.name());
		organisation.setModified(ZonedDateTime.now(ZoneOffset.UTC));
		
		organisation = organisationRepository.saveAndFlush(organisation);

		OrganisationSubscription organisationSubscriptionActive = organisationSubscriptionRepository.findByOrganisationAndStatus(organisation, OrganisationSubscriptionStatusEnum.Active.name());
		OrganisationInvoice organisationInvoiceNew = organisationInvoiceRepository.findByOrganisationSubscriptionAndStatus(organisationSubscriptionActive, OrganisationInvoiceStatusEnum.New.name());
		if (organisationInvoiceNew == null) {
			 
			// no 'new' invoice found for this org; creating one now...

			organisationInvoiceNew = OrganisationInvoice.builder()
				.invCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
				.periodFrom(ZonedDateTime.now(ZoneOffset.UTC))
				.status(OrganisationInvoiceStatusEnum.New.name())
				.eventTotal(Long.valueOf(0))
				.dataIngestTotal(Long.valueOf(0))
				.data50(false)
				.data75(false)
				.data100(false)	
				.amountTotal(Long.valueOf(0))
				.created(ZonedDateTime.now(ZoneOffset.UTC))
				.modified(ZonedDateTime.now(ZoneOffset.UTC))
				.build();
			
			OrganisationSubscription organisationSubscriptionNew = organisationSubscriptionRepository.findByOrganisationAndStatus(organisation, OrganisationSubscriptionStatusEnum.New.name());
			if (organisationSubscriptionNew != null) {
				organisationInvoiceNew.setPeriodTo(getInvoicePeriodTo(organisationInvoiceNew.getPeriodFrom(), organisationSubscriptionNew));
				organisationInvoiceNew.setOrganisationSubscription(organisationSubscriptionNew);

				organisationSubscriptionActive.setStatus(OrganisationSubscriptionStatusEnum.Discontinued.name());
				organisationSubscriptionActive.setEndDate(ZonedDateTime.now(ZoneOffset.UTC));
				organisationSubscriptionActive.setModified(ZonedDateTime.now(ZoneOffset.UTC));
				organisationSubscriptionRepository.saveAndFlush(organisationSubscriptionActive);

				organisationSubscriptionNew.setStatus(OrganisationSubscriptionStatusEnum.Active.name());
				organisationSubscriptionNew.setStartDate(ZonedDateTime.now(ZoneOffset.UTC));
				organisationSubscriptionNew.setModified(ZonedDateTime.now(ZoneOffset.UTC));
				organisationSubscriptionRepository.saveAndFlush(organisationSubscriptionNew);
			}
			else {
				organisationInvoiceNew.setPeriodTo(getInvoicePeriodTo(organisationInvoiceNew.getPeriodFrom(), organisationSubscriptionActive));
				organisationInvoiceNew.setOrganisationSubscription(organisationSubscriptionActive);
			}
			organisationInvoiceRepository.saveAndFlush(organisationInvoiceNew);
		}
	}

	private ZonedDateTime getInvoicePeriodTo(ZonedDateTime orgInvoicePeriodFrom, OrganisationSubscription organisationSubscription) throws Exception {
		return 
			(SubscriptionPricePeriodEnum.Month.name().equals(organisationSubscription.getSubscription().get("pricePeriod"))) ? orgInvoicePeriodFrom.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS) : 
			(SubscriptionPricePeriodEnum.Year.name().equals(organisationSubscription.getSubscription().get("pricePeriod"))) ? orgInvoicePeriodFrom.plus(1, ChronoUnit.YEARS).minus(1, ChronoUnit.DAYS) : null;
	}

}