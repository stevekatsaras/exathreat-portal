package com.exathreat.organisation.forensics.detections;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import com.exathreat.common.jpa.entity.MarketplaceItem;
import com.exathreat.common.jpa.entity.MarketplaceUser;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationDetection;
import com.exathreat.common.jpa.entity.enums.MarketplaceItemStateEnum;
import com.exathreat.common.jpa.repository.MarketplaceCategoryRepository;
import com.exathreat.common.jpa.repository.MarketplaceItemRepository;
import com.exathreat.common.jpa.repository.OrganisationDetectionRepository;
import com.exathreat.common.support.JsonSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListDetectionsForensicsService {

	@Autowired
	private JsonSupport jsonSupport;

	@Autowired
	private MarketplaceCategoryRepository marketplaceCategoryRepository;

	@Autowired
	private MarketplaceItemRepository marketplaceItemRepository;

	@Autowired
	private OrganisationDetectionRepository organisationDetectionRepository;
	
	@Transactional(readOnly = true)
	public void initForensicsDetections(String orgCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationDetections", organisationDetectionRepository.findByOrganisationOrderByNameAscSeverityDescVersionDesc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = false)
	public void doForensicsDisableDetection(String orgCode, String detCode, ModelMap modelMap) throws Exception {
		OrganisationDetection organisationDetection = organisationDetectionRepository.findByDetCode(detCode);
		organisationDetection.setEnabled(false);
		organisationDetection.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationDetectionRepository.saveAndFlush(organisationDetection);
	}

	@Transactional(readOnly = false)
	public void doForensicsEnableDetection(String orgCode, String detCode, ModelMap modelMap) throws Exception {
		OrganisationDetection organisationDetection = organisationDetectionRepository.findByDetCode(detCode);
		organisationDetection.setEnabled(true);
		organisationDetection.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationDetectionRepository.saveAndFlush(organisationDetection);
	}

	@Transactional(readOnly = false)
	public void doForensicsDeleteDetection(String orgCode, String detCode, ModelMap modelMap) throws Exception {
		OrganisationDetection organisationDetection = organisationDetectionRepository.findByDetCode(detCode);
		organisationDetectionRepository.delete(organisationDetection);
	}

	@Transactional(readOnly = false)
	public void doAddMyMarketplaceDetection(String orgCode, String detCode, ModelMap modelMap) throws Exception {
		OrganisationDetection organisationDetection = organisationDetectionRepository.findByDetCode(detCode);

		Map<String, Object> item = jsonSupport.toHashMap(organisationDetection);
		item.remove("id");
		item.remove("detCode");
		item.remove("indexAlias");
		item.remove("version");
		item.remove("enabled");
		item.remove("mitreAttack");
		item.remove("created");
		item.remove("modified");
		item.remove("organisationUser");
		item.remove("organisation");

		MarketplaceItem marketplaceItem = MarketplaceItem.builder()
			.itemCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.marketplaceCategory(marketplaceCategoryRepository.findByName("Detection"))
			.marketplaceUser((MarketplaceUser) modelMap.get("marketplaceUser"))
			.item(item)
			.state(MarketplaceItemStateEnum.Draft.name())
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.build();
		
		marketplaceItemRepository.saveAndFlush(marketplaceItem);
	}

}