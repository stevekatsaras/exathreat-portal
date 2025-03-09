package com.exathreat.organisation.settings.processors;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationProcessor;
import com.exathreat.common.jpa.entity.Processor;
import com.exathreat.common.jpa.repository.OrganisationProcessorRepository;
import com.exathreat.common.jpa.repository.ProcessorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListProcessorsSettingsService {
	
	@Autowired
	private OrganisationProcessorRepository organisationProcessorRepository;

	@Autowired
	private ProcessorRepository processorRepository;
	
	@Transactional(readOnly = false)
	public void initProcessors(String orgCode, ModelMap modelMap) throws Exception {
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");

		List<Processor> processors = processorRepository.findByAvailableOrderByAcronymAsc(true);
		for (Processor processor : processors) {
			OrganisationProcessor organisationProcessor = organisationProcessorRepository.findByOrganisationAndProcessor(currentOrganisation, processor);
			if (organisationProcessor == null) {
				organisationProcessorRepository.saveAndFlush(OrganisationProcessor.builder()
				.procCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
				.enabled(false)
				.created(ZonedDateTime.now(ZoneOffset.UTC))
				.modified(ZonedDateTime.now(ZoneOffset.UTC))
				.organisation(currentOrganisation)
				.processor(processor)
				.build());
			}
		}
		modelMap.addAttribute("organisationProcessors", organisationProcessorRepository.findByOrganisationOrderByProcessorAcronymAsc(currentOrganisation));
	}
}