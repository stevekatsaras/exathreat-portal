package com.exathreat.common.jpa.repository;

import com.exathreat.common.jpa.entity.Currency;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, Long> {
	
}