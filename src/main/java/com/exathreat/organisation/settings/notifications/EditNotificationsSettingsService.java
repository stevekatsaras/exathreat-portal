package com.exathreat.organisation.settings.notifications;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;

import com.exathreat.common.jpa.entity.OrganisationNotification;
import com.exathreat.common.jpa.entity.enums.OrganisationNotificationEnum;
import com.exathreat.common.jpa.repository.OrganisationNotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class EditNotificationsSettingsService {

	@Autowired
	private OrganisationNotificationRepository organisationNotificationRepository;
	
	public void getNotificationMetadata(ModelMap modelMap) throws Exception {
		modelMap.addAttribute("notificationTypes", Arrays.asList(OrganisationNotificationEnum.values()));
	}

	@Transactional(readOnly = true)
	public void initEditNotification(String orgCode, String notCode, EditNotificationsSettingsForm editNotificationsSettingsForm, ModelMap modelMap) throws Exception {
		editNotificationsSettingsForm.setOrganisationNotification(organisationNotificationRepository.findByNotCode(notCode));
	}

	@Transactional(readOnly = false)
	public void doEditNotification(String orgCode, String notCode, EditNotificationsSettingsForm editNotificationsSettingsForm, ModelMap modelMap) throws Exception {
		OrganisationNotification organisationNotificationDto = editNotificationsSettingsForm.getOrganisationNotification();
		Map<String, Object> settingsDto = organisationNotificationDto.getSettings();

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

		OrganisationNotification organisationNotification = organisationNotificationRepository.findByNotCode(notCode);
		organisationNotification.setName(organisationNotificationDto.getName());
		organisationNotification.setDescription(organisationNotificationDto.getDescription());
		organisationNotification.setType(notificationType.getType());
		organisationNotification.setTypeLabel(notificationType.getLabel());
		organisationNotification.setSettings(settings);
		organisationNotification.setModified(ZonedDateTime.now(ZoneOffset.UTC));
			
		organisationNotificationRepository.saveAndFlush(organisationNotification);
	}
}