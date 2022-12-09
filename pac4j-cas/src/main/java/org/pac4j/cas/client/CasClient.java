package org.pac4j.cas.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.cas.credentials.extractor.TicketAndLogoutRequestExtractor;
import org.pac4j.cas.redirect.CasRedirectionActionBuilder;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.QueryParameterCallbackUrlResolver;
import org.pac4j.core.logout.CasLogoutActionBuilder;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.logout.handler.LogoutHandler;

import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * <p>This class is the client to authenticate users on a CAS server for a web application in a stateful way: when trying to access a
 * protected area, the user will be redirected to the CAS server for login and then back to the application (on the callback endpoint) and
 * finally to the originally requested url.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * <p>By default, the {@link LogoutHandler} will be a {@link DefaultLogoutHandler}. Use <code>null</code> to
 * disable logout support.</p>
 *
 * <p>For proxy support, a {@link CasProxyReceptor} must be defined in the configuration (the corresponding "callback filter" must be
 * enabled) and set to the CAS configuration of this client. In that case, a {@link org.pac4j.cas.profile.CasProxyProfile} will be return
 * (instead of a {@link org.pac4j.cas.profile.CasProfile}) to be able to request proxy tickets.</p>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@ToString(callSuper = true)
public class CasClient extends IndirectClient {

    @Getter
    @Setter
    private CasConfiguration configuration = new CasConfiguration();

    public CasClient() { }

    public CasClient(final CasConfiguration configuration) {
        setConfiguration(configuration);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("configuration", configuration);
        configuration.setUrlResolver(this.getUrlResolver());

        setRedirectionActionBuilderIfUndefined(new CasRedirectionActionBuilder(configuration, this));
        setCredentialsExtractorIfUndefined(new TicketAndLogoutRequestExtractor(configuration));
        setAuthenticatorIfUndefined(new CasAuthenticator(configuration, getName(),getUrlResolver(), getCallbackUrlResolver(), callbackUrl));
        setLogoutActionBuilderIfUndefined(new CasLogoutActionBuilder(configuration.computeFinalPrefixUrl(null) + "logout",
            configuration.getPostLogoutUrlParameter()));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator());
    }

    @Override
    protected CallbackUrlResolver newDefaultCallbackUrlResolver() {
        return new QueryParameterCallbackUrlResolver(configuration.getCustomParams());
    }

    @Override
    public void notifySessionRenewal(final String oldSessionId, final WebContext context, final SessionStore sessionStore) {
        configuration.findLogoutHandler().renewSession(oldSessionId, context, sessionStore);
    }
}
