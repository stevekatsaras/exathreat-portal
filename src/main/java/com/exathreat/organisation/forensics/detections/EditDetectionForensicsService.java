package com.exathreat.organisation.forensics.detections;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationDetection;
import com.exathreat.common.jpa.repository.OrganisationDetectionRepository;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditDetectionForensicsService {

	@Autowired
	private OrganisationDetectionRepository organisationDetectionRepository;

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	public void getDetectionMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("indices", organisationIndexRepository.findByOrganisationOrderByAliasNameDescCreatedDesc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = true)
	public void initForensicsEditDetection(String orgCode, String detCode, EditDetectionForensicsForm editDetectionForensicsForm, ModelMap modelMap) throws Exception {
		editDetectionForensicsForm.setOrganisationDetection(organisationDetectionRepository.findByDetCode(detCode));
	}

	@Transactional(readOnly = false)
	public void doForensicsEditDetection(String orgCode, String detCode, EditDetectionForensicsForm editDetectionForensicsForm, ModelMap modelMap) throws Exception {
		OrganisationDetection organisationDetectionDto = editDetectionForensicsForm.getOrganisationDetection();

		OrganisationDetection organisationDetection = organisationDetectionRepository.findByDetCode(detCode);
		organisationDetection.setModified(ZonedDateTime.now(ZoneOffset.UTC));
		
		BeanUtils.copyProperties(organisationDetectionDto, organisationDetection, "id", "detCode", "version", "mitreAttack", "created", "modified", "organisationUser", "organisation");
		organisationDetectionRepository.saveAndFlush(organisationDetection);
	}
}