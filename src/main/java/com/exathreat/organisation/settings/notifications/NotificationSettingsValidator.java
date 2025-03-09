package com.exathreat.organisation.settings.notifications;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.exathreat.common.config.factory.ApplicationSettings;
import com.exathreat.common.config.factory.DebounceFactory;
import com.exathreat.common.config.factory.TelegramSettings;
import com.exathreat.common.jpa.entity.Organisation;
import com.exathreat.common.jpa.entity.OrganisationNotification;
import com.exathreat.common.jpa.entity.enums.OrganisationNotificationEnum;
import com.exathreat.common.support.MailSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class NotificationSettingsValidator {
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private DebounceFactory debounceFactory;

	@Autowired
	private MailSupport mailSupport;

	@Autowired
	private TelegramSettings telegramSettings;

	@Autowired
	private WebClient webClient;
	
	public void validateNotificationSettings(String orgCode, OrganisationNotification organisationNotificationDto, ModelMap modelMap, Errors errors) throws Exception {
		Map<String, Object> settings = organisationNotificationDto.getSettings();

		OrganisationNotificationEnum notificationType = OrganisationNotificationEnum.getNotificationEnum(organisationNotificationDto.getType());
		if (notificationType == OrganisationNotificationEnum.EMAIL) {
			String emailAddresses = (String) settings.get("emailAddresses");

			if (emailAddresses.isBlank()) {
				errors.rejectValue("organisationNotification.settings[emailAddresses]", "invalid", "Email address is required");
			}
			else {
				String[] emailAddressesArray = emailAddresses.split(";");
				for (String emailAddress : emailAddressesArray) {
					if (!EmailValidator.getInstance().isValid(emailAddress.trim())) {
						errors.rejectValue("organisationNotification.settings[emailAddresses]", "invalid", "Email address is invalid");
						break;
					}
					else if (debounceFactory.isDisposableEmailAddress(emailAddress.trim())) {
						errors.rejectValue("organisationNotification.settings[emailAddresses]", "invalid", "Email address is invalid");
						break;
					}
				}
			}
		}
		else if (notificationType == OrganisationNotificationEnum.GOOGLE) {
			String googleChatWebhoookUrl = (String) settings.get("googleChatWebhookUrl");

			if (googleChatWebhoookUrl.isBlank()) {
				errors.rejectValue("organisationNotification.settings[googleChatWebhookUrl]", "invalid", "Google Chat Webhook URL is required");
			}
		}
		else if (notificationType == OrganisationNotificationEnum.TELEGRAM) {
			String telegramBotApiToken = (String) settings.get("telegramBotApiToken");
			String telegramBotChatID = (String) settings.get("telegramBotChatID");

			if (telegramBotApiToken.isBlank()) {
				errors.rejectValue("organisationNotification.settings[telegramBotApiToken]", "invalid", "Telegram Bot Api Token is required");
			}
			if (telegramBotChatID.isBlank()) {
				errors.rejectValue("organisationNotification.settings[telegramBotChatID]", "invalid", "Telegram Bot Chat ID is required");
			}
		}
	}

	public void test(String orgCode, OrganisationNotification organisationNotificationDto, ModelMap modelMap, Errors errors) throws Exception {
		OrganisationNotificationEnum notificationType = OrganisationNotificationEnum.getNotificationEnum(organisationNotificationDto.getType());
		if (notificationType == OrganisationNotificationEnum.EMAIL) {
			testEmail(orgCode, organisationNotificationDto, modelMap);
		}
		else if (notificationType == OrganisationNotificationEnum.GOOGLE) {
			testGooglChat(orgCode, organisationNotificationDto, modelMap);
		}
		else if (notificationType == OrganisationNotificationEnum.TELEGRAM) {
			testTelegram(orgCode, organisationNotificationDto, modelMap);
		}
		modelMap.put("tested", true);
	}

	private void testEmail(String orgCode, OrganisationNotification organisationNotificationDto, ModelMap modelMap) throws Exception {
		Map<String, Object> settings = organisationNotificationDto.getSettings();
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");

		String[] emailAddresses = ((String) settings.get("emailAddresses")).split(";");
		try {
			Map<String, Object> variables = Map.of(
				"organisation", currentOrganisation, 
				"organisationNotification", organisationNotificationDto, 
				"portalUrl", applicationSettings.getRootUri() + "/", 
				"logo", "logo"
			);
			
			mailSupport.sendEmail(emailAddresses, "Test notification from Exathreat", "organisation/settings/notifications/emails/test", variables);
		}
		catch (Exception exception) {
			exception.printStackTrace();
			// ignore (unless necessary to handle it)
		}
	}

	private void testGooglChat(String orgCode, OrganisationNotification organisationNotificationDto, ModelMap modelMap) throws Exception {
		try {
			Map<String, Object> settings = organisationNotificationDto.getSettings();
			Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");

			String googleChatWebhookUrl = URLDecoder.decode((String) settings.get("googleChatWebhookUrl"), StandardCharsets.UTF_8.toString());

			ObjectNode orgWidget = objectMapper.createObjectNode();
			orgWidget.putObject("keyValue")
				.put("topLabel", "Organisation")
				.put("content", "<b>" + currentOrganisation.getOrgName() + "</b>")
				.put("icon", "DESCRIPTION");

			ObjectNode channelWidget = objectMapper.createObjectNode();
			channelWidget.putObject("keyValue")
				.put("topLabel", "Channel")
				.put("content", "<b>" + organisationNotificationDto.getName() + "</b>")
				.put("icon", "DESCRIPTION");
	
			ObjectNode exathreatLink = objectMapper.createObjectNode();
			exathreatLink.putObject("textButton")
				.put("text", "Exathreat")
				.putObject("onClick")
					.putObject("openLink")
						.put("url", applicationSettings.getRootUri() + "/" + orgCode + "/settings/notifications");
	
			ObjectNode buttonWidget = objectMapper.createObjectNode();
			buttonWidget.putArray("buttons").add(exathreatLink);
	
			ObjectNode channelSection = objectMapper.createObjectNode();
			channelSection.putArray("widgets").add(orgWidget).add(channelWidget).add(buttonWidget);

			ObjectNode infoWidget = objectMapper.createObjectNode();
			infoWidget.putObject("textParagraph").put("text", 
				"Please ignore, as someone is performing a test to ensure this notification works. " +
				"If you do not wish to receive these notifications, please contact your account administrator.");
	
			ObjectNode infoSection = objectMapper.createObjectNode();
			infoSection.putArray("widgets").add(infoWidget);

			ObjectNode footerWidget = objectMapper.createObjectNode();
			footerWidget.putObject("textParagraph").put("text", "Cheers!<br>The Exathreat team.");
	
			ObjectNode footerSection = objectMapper.createObjectNode();
			footerSection.putArray("widgets").add(footerWidget);

			ObjectNode card = objectMapper.createObjectNode();
			card.putObject("header").put("title", "Test notification from Exathreat.");
			card.putArray("sections").add(channelSection).add(infoSection).add(footerSection);

			ObjectNode notification = objectMapper.createObjectNode();
			notification.putArray("cards").add(card);

			webClient.post()
			.uri(googleChatWebhookUrl)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.body(Mono.just(notification), JsonNode.class)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
			.block();
		}
		catch (Exception exception) {
			// ignore (unless necessary to handle it)
		}
	}

	private void testTelegram(String orgCode, OrganisationNotification organisationNotificationDto, ModelMap modelMap) throws Exception {
		Map<String, Object> settings = organisationNotificationDto.getSettings();
		Organisation currentOrganisation = (Organisation) modelMap.get("currentOrganisation");

		String msg = "Test notification from Exathreat.\n\n" +
			"Organisation: <b>" + currentOrganisation.getOrgName() + "</b>\n" + 
			"Channel: <b>" + organisationNotificationDto.getName() + "</b>\n" +
			"Portal: <b>" + applicationSettings.getRootUri() + "/</b>\n\n" + 
			"Please ignore, as someone is performing a test to ensure this notification works. " + 
			"If you do not wish to receive these notifications, please contact your account administrator.\n\n" + 
			"Cheers!\n" + 
			"The Exathreat team.";

		Map<String, String> map = Map.of("chat_id", (String) settings.get("telegramBotChatID"), "text", msg, "parse_mode", "HTML");
		try {
			webClient.post()
			.uri(telegramSettings.getRootUri() + "/bot" + (String) settings.get("telegramBotApiToken") + "/sendMessage")
			.body(Mono.just(map), Map.class)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>(){})
			.block();
		}
		catch (Exception exception) {
			// ignore (unless necessary to handle it)
		}
	}
}