package edu.uchicago.adamzhang22.quotes.security;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomRequestCache extends HttpSessionRequestCache {
	/**
	 * {@inheritDoc}
	 *
	 * If the method is considered an internal request from the framework, we skip
	 * saving it.
	 *
	 * @see edu.uchicago.adamzhang22.quotes.security.SecurityUtils#isFrameworkInternalRequest(HttpServletRequest)
	 */
	@Override
	public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
		if (!edu.uchicago.adamzhang22.quotes.security.SecurityUtils.isFrameworkInternalRequest(request)) {
			super.saveRequest(request, response);
		}
	}

}