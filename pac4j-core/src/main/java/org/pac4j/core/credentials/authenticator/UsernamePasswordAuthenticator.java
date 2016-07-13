package org.pac4j.core.credentials.authenticator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.exception.HttpAction;

/**
 * This interface represents the contract to validate a username / password credentials.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface UsernamePasswordAuthenticator extends Authenticator<UsernamePasswordCredentials> {

    @Override
    void validate(UsernamePasswordCredentials credentials, WebContext context) throws HttpAction;
}
