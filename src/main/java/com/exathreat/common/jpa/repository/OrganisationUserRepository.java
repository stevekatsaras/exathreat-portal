package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganisationUserRepository extends JpaRepository<OrganisationUser, Long> {
	List<OrganisationUser> findByOrganisationOrderByEmailAddressAsc(Organisation organisation);
//	List<OrganisationUser> findByEmailAddressAndOrganisationStatus(String emailAddress, OrganisationStatusEnum status);
	List<OrganisationUser> findByEmailAddressOrderByOrganisationOrgNameAsc(String emailAddress);
	OrganisationUser findByEmailAddressAndOrganisation(String emailAddress, Organisation organisation);
	OrganisationUser findByUserCode(String userCode);
}