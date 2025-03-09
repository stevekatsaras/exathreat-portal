package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationDetection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationDetectionRepository extends JpaRepository<OrganisationDetection, Long> {
	List<OrganisationDetection> findByOrganisationOrderByNameAscSeverityDescVersionDesc(Organisation organisation);
	OrganisationDetection findByDetCode(String detCode);
}