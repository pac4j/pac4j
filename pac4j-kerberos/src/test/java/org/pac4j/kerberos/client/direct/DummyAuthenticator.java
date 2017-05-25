package org.pac4j.kerberos.client.direct;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.HttpAction;

public class DummyAuthenticator implements Authenticator<Credentials> {

    @Override
    public void validate(Credentials credentials, WebContext context) throws HttpAction {
    }

}
