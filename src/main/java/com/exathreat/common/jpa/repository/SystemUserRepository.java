package com.exathreat.common.jpa.repository;

import com.exathreat.common.jpa.entity.SystemUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUser, Long> {
	SystemUser findByEmailAddress(String emailAddress);
}