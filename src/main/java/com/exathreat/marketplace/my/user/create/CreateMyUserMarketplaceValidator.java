package com.exathreat.marketplace.my.user.create;

import com.exathreat.common.jpa.entity.MarketplaceUser;
import com.exathreat.common.jpa.repository.MarketplaceUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;

@Component
public class CreateMyUserMarketplaceValidator {

	@Autowired
	private MarketplaceUserRepository marketplaceUserRepository;

	public void validateMarketplaceUser(MarketplaceUser marketplaceUserDto, ModelMap modelMap, Errors errors) throws Exception {
		if (modelMap.get("marketplaceUser") != null) {
			errors.rejectValue("marketplaceUser.emailAddress", "invalid", "You already have a profile");
		}
		else {
			MarketplaceUser marketplaceUser = marketplaceUserRepository.findByUsername(marketplaceUserDto.getUsername());
			if (marketplaceUser != null) {
				errors.rejectValue("marketplaceUser.username", "invalid", "Username is not available");
			}
		}
	}

}