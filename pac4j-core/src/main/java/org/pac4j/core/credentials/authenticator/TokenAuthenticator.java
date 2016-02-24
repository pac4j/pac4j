package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.RequiresHttpAction;

/**
 * This interface represents the contract to validate a token credentials.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface TokenAuthenticator extends Authenticator<TokenCredentials> {

    @Override
    void validate(TokenCredentials credentials) throws RequiresHttpAction;
}
