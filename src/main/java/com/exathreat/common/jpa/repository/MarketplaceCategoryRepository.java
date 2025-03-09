package com.exathreat.common.jpa.repository;

import com.exathreat.common.jpa.entity.MarketplaceCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceCategoryRepository extends JpaRepository<MarketplaceCategory, Long> {
	MarketplaceCategory findByName(String name);
}