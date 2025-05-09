package com.exathreat.common.jpa.repository;

import com.exathreat.common.jpa.entity.Audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Long> {
	
}