package edu.uchicago.adamzhang22.quotes.security;

import edu.uchicago.adamzhang22.quotes.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CognitoLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
    private OAuth2AuthorizedClientService clientService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
    	OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
    	OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token.getName());

    	// print access token
    	System.out.println("Access Token:" + client.getAccessToken().getTokenValue());

    	DefaultOAuth2User principal = ((DefaultOAuth2User) authentication.getPrincipal());
		Cache.getInstance().setEmail(principal.getAttributes().get("email").toString());
		Cache.getInstance().setKeyword("");
		//Cache.getInstance().clearItems();
    	// print id token decoded value
    	System.out.println("Info from Id Token:" + principal.getAttributes());

		super.onAuthenticationSuccess(request, response, authentication);
	}

}
