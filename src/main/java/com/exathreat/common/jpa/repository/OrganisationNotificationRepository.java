package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationNotification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationNotificationRepository extends JpaRepository<OrganisationNotification, Long> {
	OrganisationNotification findByNotCode(String notCode);
	List<OrganisationNotification> findByOrganisationOrderByNameAsc(Organisation organisation);
}