package com.exathreat.organisation.settings.queries;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationQuery;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;
import com.exathreat.common.jpa.repository.OrganisationQueryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditQueriesSettingService {

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	@Autowired
	private OrganisationQueryRepository organisationQueryRepository;

	public void getQueriesMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("indices", organisationIndexRepository.findByOrganisationOrderByAliasNameDescCreatedDesc((Organisation) modelMap.get("currentOrganisation")));
	}
	
	@Transactional(readOnly = true)
	public void initEditQuery(String orgCode, String queryCode, EditQueriesSettingsForm editQueriesSettingsForm, ModelMap modelMap) throws Exception {
		editQueriesSettingsForm.setOrganisationQuery(organisationQueryRepository.findByQueryCode(queryCode));
	}

	@Transactional(readOnly = false)
	public void doEditQuery(String orgCode, String queryCode, EditQueriesSettingsForm editQueriesSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationQuery organisationQueryDto = editQueriesSettingsForm.getOrganisationQuery();

		OrganisationQuery organisationQuery = organisationQueryRepository.findByQueryCode(queryCode);
		organisationQuery.setName(organisationQueryDto.getName());
		organisationQuery.setQueryStr(organisationQueryDto.getQueryStr());
		organisationQuery.setDateRange(organisationQueryDto.getDateRange());
		organisationQuery.setDateFormat(organisationQueryDto.getDateFormat());
		organisationQuery.setTimeFormat(organisationQueryDto.getTimeFormat());
		organisationQuery.setIndexAlias(organisationQueryDto.getIndexAlias());
		organisationQuery.setIntervalUnit(organisationQueryDto.getIntervalUnit());
		organisationQuery.setMinDocCount(organisationQueryDto.getMinDocCount());
		organisationQuery.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationQueryRepository.saveAndFlush(organisationQuery);
	}

}