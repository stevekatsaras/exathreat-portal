package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationSubscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationSubscriptionRepository extends JpaRepository<OrganisationSubscription, Long> {
	OrganisationSubscription findByOrganisationAndStatus(Organisation organisation, String status);
	List<OrganisationSubscription> findByOrganisationOrderByIdDesc(Organisation organisation);
}