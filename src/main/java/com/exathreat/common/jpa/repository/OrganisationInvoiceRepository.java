package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationInvoice;
import com.exathreat.common.jpa.entity.OrganisationSubscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationInvoiceRepository extends JpaRepository<OrganisationInvoice, Long> {
	OrganisationInvoice findByOrganisationSubscriptionAndStatus(OrganisationSubscription organisationSubscription, String status);
	List<OrganisationInvoice> findByOrganisationSubscriptionOrganisationOrderByPeriodFromDesc(Organisation organisation);	
}