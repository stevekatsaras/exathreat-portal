package com.exathreat.login;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import com.exathreat.common.config.factory.ApplicationSettings;
import com.exathreat.common.config.factory.IdpSettings;
import com.okta.spring.boot.oauth.config.OktaOAuth2Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class LoginController {

	@Autowired
	private ApplicationSettings applicationSettings;

	@Autowired
	private IdpSettings idpSettings;
	
	@Autowired
	private OktaOAuth2Properties oktaOAuth2Properties;

	@GetMapping("/login")
	public ModelAndView login(
		@RequestParam(name = "state", required = false) String state, 
		@RequestParam(name = "nonce") String nonce, 
		HttpServletRequest request) throws Exception {

		if (state == null) {
			return new ModelAndView("redirect:" + oktaOAuth2Properties.getRedirectUri());
		}
		ModelAndView modelAndView = new ModelAndView("login");
		modelAndView.addObject("state", state);
		modelAndView.addObject("nonce", nonce);
		modelAndView.addObject("scopes", oktaOAuth2Properties.getScopes());
		modelAndView.addObject("oktaBaseUrl", new URL(new URL(oktaOAuth2Properties.getIssuer()), "/").toString());
		modelAndView.addObject("oktaClientId", oktaOAuth2Properties.getClientId());
		modelAndView.addObject("issuerUri", oktaOAuth2Properties.getIssuer());
		modelAndView.addObject("redirectUri", applicationSettings.getRootUri() + oktaOAuth2Properties.getRedirectUri());
		modelAndView.addObject("facebookId", idpSettings.getFacebookId());
		modelAndView.addObject("googleId", idpSettings.getGoogleId());
		modelAndView.addObject("microsoftId", idpSettings.getMicrosoftId());
		return modelAndView;
	}
	
}