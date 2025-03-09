package com.exathreat.marketplace.my.user.create;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.exathreat.common.jpa.entity.MarketplaceUser;
import com.exathreat.common.jpa.repository.MarketplaceUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateMyUserMarketplaceService {

	@Autowired
	private MarketplaceUserRepository marketplaceUserRepository;
	
	@Transactional(readOnly = false)
	public void doCreate(CreateMyUserMarketplaceForm createMyUserMarketplaceForm) throws Exception {
		OidcUser oidcUser = (OidcUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		MarketplaceUser marketplaceUser = MarketplaceUser.builder()
			.emailAddress(oidcUser.getEmail())
			.givenName(oidcUser.getGivenName())
			.surname(oidcUser.getFamilyName())
			.username(createMyUserMarketplaceForm.getMarketplaceUser().getUsername())
			.created(ZonedDateTime.now(ZoneOffset.UTC))
			.modified(ZonedDateTime.now(ZoneOffset.UTC))
			.build();

		marketplaceUserRepository.saveAndFlush(marketplaceUser);
	}
}