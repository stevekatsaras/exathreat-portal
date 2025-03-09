package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationQueryRepository extends JpaRepository<OrganisationQuery, Long> {
	List<OrganisationQuery> findByOrganisationOrderByNameAsc(Organisation organisation);
	List<OrganisationQuery> findByOrganisationAndEnabledOrderByNameAsc(Organisation organisation, Boolean enabled);
	OrganisationQuery findByQueryCode(String queryCode);
}