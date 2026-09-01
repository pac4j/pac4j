package org.pac4j.openid4vp.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The way the wallet returns the presentation. The high assurance interoperability profile (HAIP),
 * which the EUDI architecture and reference framework relies on, mandates {@link #DIRECT_POST_JWT}:
 * the response is encrypted and posted server to server.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@RequiredArgsConstructor
public enum ResponseMode {

    DIRECT_POST("direct_post"),
    DIRECT_POST_JWT("direct_post.jwt");

    private final String value;
}
