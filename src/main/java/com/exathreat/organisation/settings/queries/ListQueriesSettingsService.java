package com.exathreat.organisation.settings.queries;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationQuery;
import com.exathreat.common.jpa.repository.OrganisationQueryRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListQueriesSettingsService {

	@Autowired
	private OrganisationQueryRepository organisationQueryRepository;

	@Transactional(readOnly = true)
	public void initQueries(String orgCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationQueries", organisationQueryRepository.findByOrganisationOrderByNameAsc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = false)
	public void doCloneQuery(String orgCode, String queryCode, Map<String, Object> params, ModelMap modelMap) throws Exception {
		OrganisationQuery organisationQuery = organisationQueryRepository.findByQueryCode(queryCode);
		OrganisationQuery organisationQueryClone = OrganisationQuery.builder()
			.queryCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.name((String) params.get("queryName"))
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.build();

		BeanUtils.copyProperties(organisationQuery, organisationQueryClone, "id", "queryCode", "name", "created", "modified");
		organisationQueryRepository.saveAndFlush(organisationQueryClone);
	}

	@Transactional(readOnly = false)
	public void doDisableQuery(String orgCode, String queryCode, ModelMap modelMap) throws Exception {
		OrganisationQuery organisationQuery = organisationQueryRepository.findByQueryCode(queryCode);
		organisationQuery.setEnabled(false);
		organisationQuery.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationQuery = organisationQueryRepository.saveAndFlush(organisationQuery);
	}

	@Transactional(readOnly = false)
	public void doEnableQuery(String orgCode, String queryCode, ModelMap modelMap) throws Exception {
		OrganisationQuery organisationQuery = organisationQueryRepository.findByQueryCode(queryCode);
		organisationQuery.setEnabled(true);
		organisationQuery.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationQuery = organisationQueryRepository.saveAndFlush(organisationQuery);
	}

	@Transactional(readOnly = false)
	public void doDeleteQuery(String orgCode, String queryCode, ModelMap modelMap) throws Exception {
		OrganisationQuery organisationQuery = organisationQueryRepository.findByQueryCode(queryCode);
		organisationQueryRepository.delete(organisationQuery);
	}
}