package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationVisualization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationVisualizationRepository extends JpaRepository<OrganisationVisualization, Long> {
	OrganisationVisualization findByVizCode(String vizCode);
	List<OrganisationVisualization> findByOrganisationOrderByNameAsc(Organisation organisation);	
}