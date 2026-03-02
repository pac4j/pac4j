package org.pac4j.oidc.credentials.clientauth;

import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;

/**
 * Build the client authentication.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
public interface ClientAuthenticationBuilder {

    void buildClientAuthentication();

    ClientAuthentication getClientAuthentication();
}
