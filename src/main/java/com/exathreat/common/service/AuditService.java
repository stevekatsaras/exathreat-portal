package com.exathreat.common.service;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

import com.exathreat.common.jpa.entity.Audit;
import com.exathreat.common.jpa.entity.MarketplaceItem;
import com.exathreat.common.jpa.entity.MarketplaceUser;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationDetection;
import com.exathreat.common.jpa.entity.OrganisationIndex;
import com.exathreat.common.jpa.entity.OrganisationInvoice;
import com.exathreat.common.jpa.entity.OrganisationKey;
import com.exathreat.common.jpa.entity.OrganisationNotification;
import com.exathreat.common.jpa.entity.OrganisationProcessor;
import com.exathreat.common.jpa.entity.OrganisationQuery;
import com.exathreat.common.jpa.entity.OrganisationSubscription;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.OrganisationVisualization;
import com.exathreat.common.jpa.entity.SystemUser;
import com.exathreat.common.jpa.repository.AuditRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {
	public ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private AuditRepository auditRepository;

	@PostPersist
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void postPersist(Object object) throws Exception {
		audit("insert", object);
	}

	@PostRemove
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void postRemove(Object object) throws Exception {
		audit("delete", object);
	}
	
	@PostUpdate
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void postUpdate(Object object) throws Exception {
		audit("update", object);
	}

	// audit the changed entity

	private void audit(String operation, Object object) throws Exception {
		Map<String, String> entityMap = parseEntity(object);
		
		Audit audit = Audit.builder()
			.audCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.orgCode(entityMap.get("orgCode"))
			.auditor(getCurrentLoggedInUser())
			.operation(operation)
			.entityCode(entityMap.get("entityCode"))
			.entityName(entityMap.get("entityName"))
			.entity(objectMapper.writeValueAsString(object))
			.audited(ZonedDateTime.now(ZoneOffset.UTC))
			.build();
		
		auditRepository.saveAndFlush(audit);
	}

	// entity information captured during the user transaction

	private Map<String, String> parseEntity(Object object) {
		Map<String, String> entityMap = new HashMap<String, String>();

		if (object instanceof MarketplaceItem) {
			MarketplaceItem marketplaceItem = (MarketplaceItem) object;
			entityMap = buildEntityMap("", marketplaceItem.getItemCode(), marketplaceItem.getClass().getSimpleName());
		}
		else if (object instanceof MarketplaceUser) {
			MarketplaceUser marketplaceUser = (MarketplaceUser) object;
			entityMap = buildEntityMap("", marketplaceUser.getEmailAddress(), marketplaceUser.getClass().getSimpleName());
		}
		else if (object instanceof Organisation) {
			Organisation organisation = (Organisation) object;
			entityMap = buildEntityMap(organisation.getOrgCode(), organisation.getOrgCode(), organisation.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationDetection) {
			OrganisationDetection organisationDetection = (OrganisationDetection) object;
			Organisation organisation = organisationDetection.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationDetection.getDetCode(), organisationDetection.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationIndex) {
			OrganisationIndex organisationIndex = (OrganisationIndex) object;
			Organisation organisation = organisationIndex.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationIndex.getIndCode(), organisationIndex.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationInvoice) {
			OrganisationInvoice organisationInvoice = (OrganisationInvoice) object;
			Organisation organisation = organisationInvoice.getOrganisationSubscription().getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationInvoice.getInvCode(), organisationInvoice.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationKey) {
			OrganisationKey organisationKey = (OrganisationKey) object;
			Organisation organisation = organisationKey.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationKey.getKeyCode(), organisationKey.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationNotification) {
			OrganisationNotification organisationNotification = (OrganisationNotification) object;
			Organisation organisation = organisationNotification.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationNotification.getNotCode(), organisationNotification.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationProcessor) {
			OrganisationProcessor organisationProcessor = (OrganisationProcessor) object;
			Organisation organisation = organisationProcessor.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationProcessor.getProcCode(), organisationProcessor.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationQuery) {
			OrganisationQuery organisationQuery = (OrganisationQuery) object;
			Organisation organisation = organisationQuery.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationQuery.getQueryCode(), organisationQuery.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationSubscription) {
			OrganisationSubscription organisationSubscription = (OrganisationSubscription) object;
			Organisation organisation = organisationSubscription.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationSubscription.getSubCode(), organisationSubscription.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationUser) {
			OrganisationUser organisationUser = (OrganisationUser) object;
			Organisation organisation = organisationUser.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationUser.getUserCode(), organisationUser.getClass().getSimpleName());
		}
		else if (object instanceof OrganisationVisualization) {
			OrganisationVisualization organisationvVisualization = (OrganisationVisualization) object;
			Organisation organisation = organisationvVisualization.getOrganisation();
			entityMap = buildEntityMap(organisation.getOrgCode(), organisationvVisualization.getVizCode(), organisationvVisualization.getClass().getSimpleName());
		}
		else if (object instanceof SystemUser) {
			SystemUser systemUser = (SystemUser) object;
			entityMap = buildEntityMap("", systemUser.getEmailAddress(), systemUser.getClass().getSimpleName());
		}
		return entityMap;
	}

	private Map<String, String> buildEntityMap(String orgCode, String entityCode, String entityName) {
		Map<String, String> entityMap = new HashMap<String, String>();
		entityMap.put("orgCode", orgCode);
		entityMap.put("entityCode", entityCode);
		entityMap.put("entityName", entityName);
		return entityMap;
	}

	// current logged in user performing the transaction

	private String getCurrentLoggedInUser() {
		OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return oidcUser.getEmail();
	}
}