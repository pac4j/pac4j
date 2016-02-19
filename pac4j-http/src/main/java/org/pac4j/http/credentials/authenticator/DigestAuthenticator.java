package org.pac4j.http.credentials.authenticator;

import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.http.credentials.UsernamePasswordCredentials;

/**
 * This interface represents the contract to validate digest credentials.
 *
 * @author Mircea Carasel
 */
public interface DigestAuthenticator extends Authenticator<DigestCredentials> {

    @Override
    void validate(DigestCredentials credentials);
}
