package com.exathreat.organisation.create;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.exathreat.common.config.factory.ElasticsearchFactory;
import com.exathreat.common.jpa.entity.BusinessType;
import com.exathreat.common.jpa.entity.Country;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationIndex;
import com.exathreat.common.jpa.entity.OrganisationInvoice;
import com.exathreat.common.jpa.entity.OrganisationKey;
import com.exathreat.common.jpa.entity.OrganisationNotification;
import com.exathreat.common.jpa.entity.OrganisationProcessor;
import com.exathreat.common.jpa.entity.OrganisationSubscription;
import com.exathreat.common.jpa.entity.OrganisationUser;
import com.exathreat.common.jpa.entity.Processor;
import com.exathreat.common.jpa.entity.Subscription;
import com.exathreat.common.jpa.entity.enums.OrganisationIndexTypeEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationInvoiceStatusEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationKeyTypeEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationNotificationEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationStatusEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationSubscriptionStatusEnum;
import com.exathreat.common.jpa.entity.enums.OrganisationUserRoleEnum;
import com.exathreat.common.jpa.entity.enums.SubscriptionPricePeriodEnum;
import com.exathreat.common.jpa.repository.BusinessTypeRepository;
import com.exathreat.common.jpa.repository.CountryRepository;
import com.exathreat.common.jpa.repository.OrganisationIndexRepository;
import com.exathreat.common.jpa.repository.OrganisationInvoiceRepository;
import com.exathreat.common.jpa.repository.OrganisationKeyRepository;
import com.exathreat.common.jpa.repository.OrganisationNotificationRepository;
import com.exathreat.common.jpa.repository.OrganisationProcessorRepository;
import com.exathreat.common.jpa.repository.OrganisationRepository;
import com.exathreat.common.jpa.repository.OrganisationSubscriptionRepository;
import com.exathreat.common.jpa.repository.OrganisationUserRepository;
import com.exathreat.common.jpa.repository.ProcessorRepository;
import com.exathreat.common.jpa.repository.SubscriptionRepository;
import com.exathreat.common.support.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class CreateOrganisationService {
	public ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired
	private BusinessTypeRepository businessTypeRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private ElasticsearchFactory elasticsearchFactory;

	@Autowired
	private JsonSupport jsonSupport;

	@Autowired
	private OrganisationRepository organisationRepository;

	@Autowired
	private OrganisationIndexRepository organisationIndexRepository;

	@Autowired
	private OrganisationInvoiceRepository organisationInvoiceRepository;

	@Autowired
	private OrganisationKeyRepository organisationKeyRepository;

	@Autowired
	private OrganisationNotificationRepository organisationNotificationRepository;

	@Autowired
	private OrganisationProcessorRepository organisationProcessorRepository;

	@Autowired
	private OrganisationSubscriptionRepository organisationSubscriptionRepository;

	@Autowired
	private OrganisationUserRepository organisationUserRepository;

	@Autowired
	private ProcessorRepository processorRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Transactional(readOnly = true)
	public void getOrganisationMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("businessTypes", businessTypeRepository.findAllByOrderByNameAsc());
		modelMap.addAttribute("countries", countryRepository.findAllByOrderByNameAsc());
		modelMap.addAttribute("subscriptions", subscriptionRepository.findByEnabledOrderByPriceAmountAsc(true));
	}

	public void initCreate(CreateOrganisationForm createOrganisationForm) throws Exception {
		createOrganisationForm.setOrganisation(Organisation.builder()
			.businessType(BusinessType.builder().id(6L).build())	// default business type: Company
			.country(Country.builder().id(13L).build())						// default country: Australia
			.build());
		createOrganisationForm.setOrganisationSubscription(OrganisationSubscription.builder()
			.subscription(new HashMap<String, Object>(Map.of("id", 1L)))	// default subscription: 'free'
			.build());
	}

	@Transactional(readOnly = false)
	public String doCreate(CreateOrganisationForm createOrganisationForm, ModelMap modelMap) throws Exception {
		Organisation organisationDto = createOrganisationForm.getOrganisation();
		OrganisationSubscription organisationSubscriptionDto = createOrganisationForm.getOrganisationSubscription();

		BusinessType businessType = businessTypeRepository.getOne(organisationDto.getBusinessType().getId());
		Country country = countryRepository.getOne(organisationDto.getCountry().getId());
		Subscription subscription = subscriptionRepository.getOne(Long.parseLong((String) organisationSubscriptionDto.getSubscription().get("id")));

		// create org in db

		Organisation organisation = Organisation.builder()
			.orgCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.orgName(organisationDto.getOrgName())
			.businessType(businessType)
			.status(OrganisationStatusEnum.Active.name())
			.signupDate(ZonedDateTime.now(ZoneOffset.UTC))
			.city(organisationDto.getCity())
			.state(organisationDto.getState())
			.postcode(organisationDto.getPostcode())
			.country(country)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.build();

		organisation = organisationRepository.saveAndFlush(organisation);

		// create user in db

		OrganisationUser organisationUser = OrganisationUser.builder()
			.userCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.emailAddress((String) modelMap.get("emailAddress"))
			.givenName((String) modelMap.get("givenName"))
			.surname((String) modelMap.get("surname"))
			.userOwner(true)
			.userRole(OrganisationUserRoleEnum.ADMIN.name())
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(organisation)
			.build();

		organisationUser = organisationUserRepository.saveAndFlush(organisationUser);

		// create new api key in db

		OrganisationKey organisationKey = OrganisationKey.builder()
			.keyCode(UUID.randomUUID().toString())
			.name("API (default)")
			.description("Default API key. To be used with any gateway.")
			.keyType(OrganisationKeyTypeEnum.api.name())
			.enabled(true)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(organisation)
			.build();

		organisationKey = organisationKeyRepository.saveAndFlush(organisationKey);

		// create default notification channel (email)

		OrganisationNotification organisationNotification = OrganisationNotification.builder()
			.notCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.name("Email (default)")
			.description("Default email notification channel. Alerts forwarded to this channel will be sent as email.")
			.type(OrganisationNotificationEnum.EMAIL.getType())
			.typeLabel(OrganisationNotificationEnum.EMAIL.getLabel())
			.enabled(true)
			.settings(Map.of("emailAddresses", organisationUser.getEmailAddress()))
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(organisation)
			.build();
	
		organisationNotificationRepository.saveAndFlush(organisationNotification);		

		// assign all available processors to org in db (turn-off by default)

		List<OrganisationProcessor> organisationProcessors = new ArrayList<OrganisationProcessor>();
		List<Processor> processors = processorRepository.findByAvailableOrderByAcronymAsc(true);
		for (Processor processor : processors) {
			organisationProcessors.add(OrganisationProcessor.builder()
				.procCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
				.enabled(false)
				.created(ZonedDateTime.now(ZoneOffset.UTC))
				.modified(ZonedDateTime.now(ZoneOffset.UTC))
				.organisation(organisation)
				.processor(processor)
				.build());
		}

		organisationProcessors = organisationProcessorRepository.saveAll(organisationProcessors);

		// attach subscription to org in db

		OrganisationSubscription organisationSubscription = OrganisationSubscription.builder()
			.subCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.startDate(ZonedDateTime.now(ZoneOffset.UTC))
			.status(OrganisationSubscriptionStatusEnum.Active.name())
			.subscription(jsonSupport.toHashMap(subscription))
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(organisation)
			.build();
	
		organisationSubscription = organisationSubscriptionRepository.saveAndFlush(organisationSubscription);

		// generate new invoice placeholder for org in db

		ZonedDateTime orgInvoicePeriodFrom = ZonedDateTime.now(ZoneOffset.UTC);
		ZonedDateTime orgInvoicePeriodTo = 
			(SubscriptionPricePeriodEnum.Month.name().equals(subscription.getPricePeriod())) ? orgInvoicePeriodFrom.plus(1, ChronoUnit.MONTHS).minus(1, ChronoUnit.DAYS) : 
			(SubscriptionPricePeriodEnum.Year.name().equals(subscription.getPricePeriod())) ? orgInvoicePeriodFrom.plus(1, ChronoUnit.YEARS).minus(1, ChronoUnit.DAYS) : null;
		
		OrganisationInvoice organisationInvoice = OrganisationInvoice.builder()
			.invCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.periodFrom(orgInvoicePeriodFrom)
			.periodTo(orgInvoicePeriodTo)
			.status(OrganisationInvoiceStatusEnum.New.name())
			.eventTotal(Long.valueOf(0))
			.dataIngestTotal(Long.valueOf(0))
			.data50(false)
			.data75(false)
			.data100(false)
			.amountTotal(Long.valueOf(0))
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisationSubscription(organisationSubscription)
			.build();

		organisationInvoice = organisationInvoiceRepository.saveAndFlush(organisationInvoice);

		// create Elasticsearch DATA & ALERT aliases and indices

		String esDataAlias = "org-" + organisation.getOrgCode() + "-v1-data";
		String esDataIndex = esDataAlias + "-000001";
		Integer esDataShards = 1;

		String esAlertAlias = "org-" + organisation.getOrgCode() + "-v1-alert";
		String esAlertIndex = esAlertAlias + "-000001";
		Integer esAlertShards = 1;

		elasticsearchFactory.createIndex(esDataAlias, esDataIndex, esDataShards);
		elasticsearchFactory.createIndex(esAlertAlias, esAlertIndex, esAlertShards);

		// add Elasticsearch aliases, indices in db

		OrganisationIndex organisationDataIndex = OrganisationIndex.builder()
			.indCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.aliasName(esDataAlias)
			.indexName(esDataIndex)
			.indexType(OrganisationIndexTypeEnum.data.name())
			.retentionDays(30)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(organisation)
			.build();

		OrganisationIndex organisationAlertIndex = OrganisationIndex.builder()
			.indCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.aliasName(esAlertAlias)
			.indexName(esAlertIndex)
			.indexType(OrganisationIndexTypeEnum.alert.name())
			.retentionDays(30)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(organisation)
			.build();

		organisationDataIndex = organisationIndexRepository.saveAndFlush(organisationDataIndex);
		organisationAlertIndex = organisationIndexRepository.saveAndFlush(organisationAlertIndex);

		return organisation.getOrgCode();
	}
}