package org.pac4j.core.util.security;

import org.pac4j.core.engine.SecurityLogic;
import org.pac4j.core.http.adapter.HttpActionAdapter;

/**
 * Mock of a security endpoint.
 *
 * @author Jerome LELEU
 * @since 5.5.0
 */
public class MockSecurityEndpoint implements SecurityEndpoint{

    private String clients;

    private String authorizers;

    private String matchers;

    private HttpActionAdapter httpActionAdapter;

    private SecurityLogic securityLogic;

    public String getClients() {
        return clients;
    }

    @Override
    public void setClients(final String clients) {
        this.clients = clients;
    }

    public String getAuthorizers() {
        return authorizers;
    }

    @Override
    public void setAuthorizers(final String authorizers) {
        this.authorizers = authorizers;
    }

    public String getMatchers() {
        return matchers;
    }

    @Override
    public void setMatchers(final String matchers) {
        this.matchers = matchers;
    }

    public HttpActionAdapter getHttpActionAdapter() {
        return httpActionAdapter;
    }

    @Override
    public void setHttpActionAdapter(final HttpActionAdapter httpActionAdapter) {
        this.httpActionAdapter = httpActionAdapter;
    }

    public SecurityLogic getSecurityLogic() {
        return securityLogic;
    }

    @Override
    public void setSecurityLogic(final SecurityLogic securityLogic) {
        this.securityLogic = securityLogic;
    }
}
