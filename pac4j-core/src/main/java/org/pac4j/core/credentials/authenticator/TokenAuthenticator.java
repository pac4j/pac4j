package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.HttpAction;

/**
 * This interface represents the contract to validate a token credentials.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public interface TokenAuthenticator extends Authenticator<TokenCredentials> {

    @Override
    void validate(TokenCredentials credentials, WebContext context) throws HttpAction;
}
