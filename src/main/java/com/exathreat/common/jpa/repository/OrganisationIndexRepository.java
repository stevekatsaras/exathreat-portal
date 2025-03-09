package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationIndex;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationIndexRepository extends JpaRepository<OrganisationIndex, Long> {
	OrganisationIndex findByIndCode(String indCode);
	List<OrganisationIndex> findByOrganisationOrderByAliasNameDescCreatedDesc(Organisation organisation);
}