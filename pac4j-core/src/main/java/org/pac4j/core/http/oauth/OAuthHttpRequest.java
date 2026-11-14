package org.pac4j.core.http.oauth;

import java.util.Map;

/**
 * An HTTP request about to be sent to the OAuth server, as the HTTP client receives it: built and,
 * when needed, already signed.
 *
 * @param verb the method (GET, POST...)
 * @param url the URL, query string included
 * @param headers the headers, with case-insensitive names: they can be added, replaced or removed
 * @author Jerome LELEU
 * @since 6.7.0
 */
public record OAuthHttpRequest(String verb, String url, Map<String, String> headers) {
}
