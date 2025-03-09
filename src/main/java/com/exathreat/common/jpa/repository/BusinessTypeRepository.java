package com.exathreat.common.jpa.repository;

import java.util.List;

import com.exathreat.common.jpa.entity.BusinessType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessTypeRepository extends JpaRepository<BusinessType, Long> {
	List<BusinessType> findAllByOrderByNameAsc();
}