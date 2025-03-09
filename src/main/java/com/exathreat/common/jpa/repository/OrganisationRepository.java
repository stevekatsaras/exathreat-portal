package com.exathreat.common.jpa.repository;

import com.exathreat.common.jpa.entity.Organisation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
	Organisation findByOrgCode(String orgCode);
}