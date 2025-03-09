package com.exathreat.common.jpa.entity.enums;

public enum OrganisationNotificationEnum {
	EMAIL("email", "Email"),
	GOOGLE("google", "Google Chat"),
	TELEGRAM("telegram", "Telegram");
	
	private final String type;
	private final String label;

	OrganisationNotificationEnum(String type, String label) {
		this.type = type;
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public static OrganisationNotificationEnum getNotificationEnum(String type) {
		OrganisationNotificationEnum notification = null;
		if (EMAIL.getType().equals(type)) {
			notification = EMAIL;
		}
		else if (GOOGLE.getType().equals(type)) {
			notification = GOOGLE;
		}
		else if (TELEGRAM.getType().equals(type)) {
			notification = TELEGRAM;
		}
		return notification;
	}
}