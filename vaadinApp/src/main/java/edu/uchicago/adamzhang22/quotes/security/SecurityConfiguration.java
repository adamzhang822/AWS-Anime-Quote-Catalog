package edu.uchicago.adamzhang22.quotes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

	@Autowired
	private CognitoProperties cognitoProperties;

	/**
	 *
	 * Used to cache request url in http session for redirection after valid login
	 *
	 */
	@Bean
	public CustomRequestCache requestCache() {
		return new CustomRequestCache();
	}

	@Configuration
	public static class MoreSecurityConfiguration extends WebSecurityConfigurerAdapter {

		@Autowired
		private CognitoLoginSuccessHandler cognitoLoginSuccessHandler;

		@Autowired
		private CognitoLogoutSuccessHandler cognitoLogoutSuccessHandler;

		@Autowired
		private CustomRequestCache requestCache;

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// Not using Spring CSRF here to be able to use plain HTML for the login page
			http.csrf().disable()
					// Register our CustomRequestCache, that saves unauthorized access attempts, so
					// the user is redirected after login.
					.requestCache().requestCache(requestCache)
					// Restrict access to our application.
					.and().authorizeRequests()

					// Allow all flow internal requests.
					.requestMatchers(SecurityUtils::isFrameworkInternalRequest).permitAll()

					// allow vaadin static urls
					.antMatchers(
							// Vaadin Flow static resources
							"/VAADIN/**",
							// the standard favicon URI
							"/favicon.ico",
							// the robots exclusion standard
							"/robots.txt",
							// web application manifest
							"/manifest.webmanifest", "/sw.js", "/offline-page.html",
							// icons and images
							"/icons/**", "/images/**",
							// (development mode) static resources
							"/frontend/**",
							// (development mode) webjars
							"/webjars/**",
							// (development mode) H2 debugging console
							"/h2-console/**",
							// (production mode) static resources
							"/frontend-es5/**", "/frontend-es6/**")
					.permitAll()

					// Allow all requests by logged in users.
					.anyRequest().authenticated()

					// Configure the login page.
					.and().oauth2Login().successHandler(cognitoLoginSuccessHandler)

					// Configure logout
					.and().logout().logoutSuccessHandler(cognitoLogoutSuccessHandler);
			// .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
		}
	}

	@Bean
	public ClientRegistrationRepository clientRegistrationRepository() {
		return new InMemoryClientRegistrationRepository(cognitoClientRegistration());
	}

	private ClientRegistration cognitoClientRegistration() {
		return ClientRegistration.withRegistrationId("cognito").clientId(cognitoProperties.getClientId())
				.clientSecret(cognitoProperties.getClientSecret())
				.clientAuthenticationMethod(ClientAuthenticationMethod.POST)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.redirectUri(cognitoProperties.getRedirectUri()).scope(cognitoProperties.getScope())
				.authorizationUri(cognitoProperties.getAuthorizationUri()).tokenUri(cognitoProperties.getTokenUri())
				.userInfoUri(cognitoProperties.getUserInfoUri())
				.userNameAttributeName(cognitoProperties.getUserNameAttribute())
				.jwkSetUri(cognitoProperties.getJwkSetUri()).clientName(cognitoProperties.getClientName()).build();
	}
}