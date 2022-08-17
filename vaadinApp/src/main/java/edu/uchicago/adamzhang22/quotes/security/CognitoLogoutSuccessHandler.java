package edu.uchicago.adamzhang22.quotes.security;

import edu.uchicago.adamzhang22.quotes.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@Component
public class CognitoLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

	@Autowired
	private CognitoProperties cognitoProperties;

	@Override
	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) {

		return UriComponentsBuilder.fromUri(URI.create(cognitoProperties.getLogoutEndpoint()))
				.queryParam("client_id", cognitoProperties.getClientId())
				.queryParam("logout_uri", cognitoProperties.getLogoutUri())
				.encode(StandardCharsets.UTF_8).build().toUriString();
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		super.onLogoutSuccess(request, response, authentication);
		Cache.getInstance().setEmail("default@default.com");
	}
}
