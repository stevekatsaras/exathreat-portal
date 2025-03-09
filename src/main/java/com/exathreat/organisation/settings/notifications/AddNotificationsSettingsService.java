package com.exathreat.organisation.settings.notifications;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationNotification;
import com.exathreat.common.jpa.entity.enums.OrganisationNotificationEnum;
import com.exathreat.common.jpa.repository.OrganisationNotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class AddNotificationsSettingsService {

	@Autowired
	private OrganisationNotificationRepository organisationNotificationRepository;

	public void getNotificationMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("notificationTypes", Arrays.asList(OrganisationNotificationEnum.values()));
	}

	public void initAddNotification(AddNotificationsSettingsForm addNotificationsSettingsForm) throws Exception {
		addNotificationsSettingsForm.setOrganisationNotification(OrganisationNotification.builder()
			.type(OrganisationNotificationEnum.EMAIL.getType())
			.settings(new HashMap<String, Object>())
			.build());
	}

	@Transactional(readOnly = false)
	public void doAddNotification(String orgCode, AddNotificationsSettingsForm addNotificationsSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationNotification organisationNotificationDto = addNotificationsSettingsForm.getOrganisationNotification();
		Map<String, Object> settingsDto = organisationNotificationDto.getSettings();

		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");
		OrganisationNotificationEnum notificationType = OrganisationNotificationEnum.getNotificationEnum(organisationNotificationDto.getType());
		
		Map<String, Object> settings = null;
		if (notificationType == OrganisationNotificationEnum.EMAIL) {
			settings = Map.of("emailAddresses", settingsDto.get("emailAddresses"));
		}
		else if (notificationType == OrganisationNotificationEnum.GOOGLE) {
			settings = Map.of("googleChatWebhookUrl", settingsDto.get("googleChatWebhookUrl"));
		}
		else if (notificationType == OrganisationNotificationEnum.TELEGRAM) {
			settings = Map.of("telegramBotApiToken", settingsDto.get("telegramBotApiToken"), "telegramBotChatID", settingsDto.get("telegramBotChatID"));
		}

		OrganisationNotification organisationNotification = OrganisationNotification.builder()
			.notCode(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12))
			.name(organisationNotificationDto.getName())
			.description(organisationNotificationDto.getDescription())
			.type(notificationType.getType())
			.typeLabel(notificationType.getLabel())
			.enabled(true)
			.settings(settings)
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.organisation(currentOrganisation)
			.build();
			
		organisationNotificationRepository.saveAndFlush(organisationNotification);
	}
}