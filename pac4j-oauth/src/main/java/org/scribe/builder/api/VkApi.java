package org.scribe.builder.api;

import org.scribe.extractors.AccessTokenExtractor;
import org.scribe.extractors.JsonTokenExtractor;
import org.scribe.model.OAuthConfig;
import org.scribe.utils.OAuthEncoder;
import org.scribe.utils.Preconditions;

/**
 * This class represents the OAuth API implementation for Vk. It could be part
 * of the Scribe library.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class VkApi extends DefaultApi20 {

	private static final String API_VERSION = "5.12";
	private static final String AUTHORIZE_URL = "https://oauth.vk.com/authorize?client_id=%s&redirect_uri=%s&response_type=code&v=" + API_VERSION;
	private static final String SCOPED_AUTHORIZE_URL = AUTHORIZE_URL + "&scope=%s";

	@Override
	public String getAccessTokenEndpoint() {
		return "https://oauth.vk.com/access_token";
	}

	@Override
	public String getAuthorizationUrl(final OAuthConfig config) {
		Preconditions.checkValidUrl(config.getCallback(), "Must provide a valid url as callback. Vk does not support OOB");

		// Append scope if present
		if (config.hasScope()) {
			return String.format(SCOPED_AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()), OAuthEncoder.encode(config.getScope()));
		} else {
			return String.format(AUTHORIZE_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()));
		}
	}

	@Override
	public AccessTokenExtractor getAccessTokenExtractor() {
		return new JsonTokenExtractor();
	}
}
