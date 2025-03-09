package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.MarketplaceItem;
import com.exathreat.common.jpa.entity.MarketplaceUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketplaceItemRepository extends JpaRepository<MarketplaceItem, Long> {
	MarketplaceItem findByItemCode(String itemCode);
	List<MarketplaceItem> findByMarketplaceUser(MarketplaceUser marketplaceUser);
	List<MarketplaceItem> findByStateOrderByCreatedDesc(String state);
}