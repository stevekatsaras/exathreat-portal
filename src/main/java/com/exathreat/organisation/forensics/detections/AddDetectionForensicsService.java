package com.exathreat.organisation.forensics.detections;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationDetection;
import com.exathreat.common.jpa.entity.OrganisationIndex;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.repository.OrganisationDetectionRepository;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class AddDetectionForensicsService {

	@Autowired
	private OrganisationDetectionRepository organisationDetectionRepository;

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	public void getDetectionMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("indices", organisationIndexRepository.findByOrganisationOrderByAliasNameDescCreatedDesc((Organisation) modelMap.get("currentOrganisation")));
	}
	
	@SuppressWarnings("unchecked")
	public void initForensicsAddDetection(String orgCode, AddDetectionForensicsForm addDetectionForensicsForm, ModelMap modelMap) throws Exception {
		OrganisationIndex organisationIndex = ((List<OrganisationIndex>) modelMap.get("indices")).get(0);
		
		addDetectionForensicsForm.setOrganisationDetection(OrganisationDetection.builder()
			.indexAlias(organisationIndex.getAliasName())
			.dateRange("Last 1 hour")
			.dateFormat((String) modelMap.get("javaDateFormat"))
			.timeFormat((String) modelMap.get("javaTimeFormat"))
			.severity(1)
			.riskScore(50)
			.runtime(5)
			.runtimeUnit("minute")
			.enabled(false)
			.build());
	}

	@Transactional(readOnly = false)
	public void doForensicsAddDetection(String orgCode, AddDetectionForensicsForm addDetectionForensicsForm, ModelMap modelMap) throws Exception {
		OrganisationDetection organisationDetectionDto = addDetectionForensicsForm.getOrganisationDetection();

		OrganisationDetection organisationDetection = OrganisationDetection.builder()
			.detCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.version(1)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisationUser((OrganisationUser) modelMap.get("loggedInUser"))
			.organisation((Organisation) modelMap.get("currentOrganisation"))
			.build();
		
		BeanUtils.copyProperties(organisationDetectionDto, organisationDetection, "detCode", "version", "created", "modified", "organisationUser", "organisation");
		organisationDetectionRepository.saveAndFlush(organisationDetection);
	}
}