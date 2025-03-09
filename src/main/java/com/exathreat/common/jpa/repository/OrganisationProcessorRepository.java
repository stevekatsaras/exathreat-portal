package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationProcessor;
import com.exathreat.common.jpa.entity.Processor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationProcessorRepository extends JpaRepository<OrganisationProcessor, Long> {
	List<OrganisationProcessor> findByOrganisationOrderByProcessorAcronymAsc(Organisation organisation);
	OrganisationProcessor findByOrganisationAndProcessor(Organisation organisation, Processor processor);
	OrganisationProcessor findByProcCode(String procCode);
}