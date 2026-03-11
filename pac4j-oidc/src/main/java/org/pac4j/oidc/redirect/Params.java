package org.pac4j.oidc.redirect;

import java.util.Map;

/**
 * Parameters for the redirection.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public record Params(Map<String, String> main, Map<String, String> extra) {
}
