package com.exathreat.common.jpa.repository;

import com.exathreat.common.jpa.entity.MarketplaceUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceUserRepository extends JpaRepository<MarketplaceUser, Long> {
	MarketplaceUser findByEmailAddress(String emailAddress);
	MarketplaceUser findByUsername(String username);
}