package com.exathreat.common.config;

import com.exathreat.common.config.security.AjaxTimeoutFilter;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.addFilterAfter(new AjaxTimeoutFilter(), ExceptionTranslationFilter.class)
			.authorizeRequests()
				.antMatchers(HttpMethod.GET, "/login", "/actuator/**", "/static/**", "/img/**").permitAll()
				.anyRequest().authenticated()
			.and().csrf()
			.and().headers().frameOptions().sameOrigin()
			.and().logout().deleteCookies().invalidateHttpSession(true).logoutSuccessUrl("/")
			.and().oauth2Client()
			.and().oauth2Login().redirectionEndpoint().baseUri("/authorization-code/callback*");
	}
}