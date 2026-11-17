package org.pac4j.core.http.oauth;

import java.util.Map;

/**
 * An HTTP response received from the OAuth server.
 *
 * @param code the status code
 * @param headers the headers, with case-insensitive names
 * @param body the body (can be null)
 * @author Jerome LELEU
 * @since 6.7.0
 */
public record OAuthHttpResponse(int code, Map<String, String> headers, String body) {
}
