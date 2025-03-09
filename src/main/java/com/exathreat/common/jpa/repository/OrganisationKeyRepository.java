package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationKey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationKeyRepository extends JpaRepository<OrganisationKey, Long> {
	OrganisationKey findByKeyCode(String keyCode);
	List<OrganisationKey> findByOrganisationOrderByIdDesc(Organisation organisation);
}