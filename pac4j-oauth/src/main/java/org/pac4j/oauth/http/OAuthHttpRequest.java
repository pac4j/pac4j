package org.pac4j.oauth.http;

import com.github.scribejava.core.model.Verb;

import java.util.Map;

/**
 * An HTTP request ScribeJava is about to send to the OAuth server, as its HTTP client receives it: built and,
 * when needed, already signed.
 *
 * @param verb the method
 * @param url the URL, query string included
 * @param headers the headers, with case-insensitive names: they can be added, replaced or removed
 * @author Jerome LELEU
 * @since 6.7.0
 */
public record OAuthHttpRequest(Verb verb, String url, Map<String, String> headers) {
}
