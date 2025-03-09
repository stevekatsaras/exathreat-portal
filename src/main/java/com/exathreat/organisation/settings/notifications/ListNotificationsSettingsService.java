package com.exathreat.organisation.settings.notifications;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationNotification;
import com.exathreat.common.jpa.repository.OrganisationNotificationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

@Service
public class ListNotificationsSettingsService {

	@Autowired
	private OrganisationNotificationRepository organisationNotificationRepository;
	
	@Transactional(readOnly = false)
	public void initNotifications(String orgCode, ModelMap modelMap) throws Exception {
		modelMap.addAttribute("organisationNotifications", organisationNotificationRepository.findByOrganisationOrderByNameAsc((Organisation) modelMap.get("currentOrganisation")));
	}

	@Transactional(readOnly = false)
	public void doDisableNotification(String orgCode, String notCode, ModelMap modelMap) throws Exception {
		OrganisationNotification organisationNotification = organisationNotificationRepository.findByNotCode(notCode);
		organisationNotification.setEnabled(false);
		organisationNotification.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationNotification = organisationNotificationRepository.saveAndFlush(organisationNotification);
	}

	@Transactional(readOnly = false)
	public void doEnableNotification(String orgCode, String notCode, ModelMap modelMap) throws Exception {
		OrganisationNotification organisationNotification = organisationNotificationRepository.findByNotCode(notCode);
		organisationNotification.setEnabled(true);
		organisationNotification.setModified(ZonedDateTime.now(ZoneOffset.UTC));

		organisationNotification = organisationNotificationRepository.saveAndFlush(organisationNotification);
	}

	@Transactional(readOnly = false)
	public void doDeleteNotification(String orgCode, String notCode, ModelMap modelMap) throws Exception {
		OrganisationNotification organisationNotification = organisationNotificationRepository.findByNotCode(notCode);
		organisationNotificationRepository.delete(organisationNotification);
	}
}