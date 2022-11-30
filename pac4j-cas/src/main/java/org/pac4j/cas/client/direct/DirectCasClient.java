package org.pac4j.cas.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.cas.authorization.DefaultCasAuthorizationGenerator;
import org.pac4j.cas.client.CasProxyReceptor;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasAuthenticator;
import org.pac4j.cas.redirect.CasRedirectionActionBuilder;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.ParameterExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.callback.NoParameterCallbackUrlResolver;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.HttpActionHelper;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * <p>This class is the direct client to authenticate users on a CAS server for a web application in a stateless way: when trying to access
 * a protected area, the user will be redirected to the CAS server for login and then back directly to this originally requested url.</p>
 *
 * <p>You should generally use the {@link org.pac4j.cas.client.CasClient} instead (this one is very specific and was designed for OAuth /
 * OpenID Connect implementations in the CAS server.</p>
 *
 * <p>The configuration can be defined via the {@link #configuration} object.</p>
 *
 * <p>As no session is meant to be created, this client does not handle CAS logout requests.</p>
 *
 * <p>For proxy support, a {@link CasProxyReceptor} must be defined in the configuration (the corresponding "callback filter" must be
 * enabled) and set to the CAS configuration of this client. In that case, a {@link org.pac4j.cas.profile.CasProxyProfile} will be return
 * (instead of a {@link org.pac4j.cas.profile.CasProfile}) to be able to request proxy tickets.</p>
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@ToString(callSuper = true)
@Getter
@Setter
public class DirectCasClient extends DirectClient {

    private CasConfiguration configuration;

    private UrlResolver urlResolver = new DefaultUrlResolver();

    private CallbackUrlResolver callbackUrlResolver = new NoParameterCallbackUrlResolver();

    public DirectCasClient() { }

    public DirectCasClient(final CasConfiguration casConfiguration) {
        this.configuration = casConfiguration;
    }

    @Override
    protected void internalInit(final boolean forceReinits) {
        assertNotNull("configuration", this.configuration);
        assertTrue(!configuration.isGateway(), "the DirectCasClient can not support gateway to avoid infinite loops");

        defaultCredentialsExtractor(new ParameterExtractor(CasConfiguration.TICKET_PARAMETER, true, false));
        // only a fake one for the initialization as we will build a new one with the current url for each request
        super.defaultAuthenticator(new CasAuthenticator(configuration, getName(), urlResolver, callbackUrlResolver, "fake"));
        addAuthorizationGenerator(new DefaultCasAuthorizationGenerator());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context, final SessionStore sessionStore,
                                                        final ProfileManagerFactory profileManagerFactory) {
        init();
        try {
            var callbackUrl = callbackUrlResolver.compute(urlResolver, context.getFullRequestURL(), getName(), context);
            val loginUrl = configuration.computeFinalLoginUrl(context);

            val credentials = getCredentialsExtractor().extract(context, sessionStore, profileManagerFactory);
            if (!credentials.isPresent()) {
                // redirect to the login page
                val redirectionUrl = CasRedirectionActionBuilder.constructRedirectUrl(loginUrl, CasConfiguration.SERVICE_PARAMETER,
                        callbackUrl, configuration.isRenew(), false, null);
                logger.debug("redirectionUrl: {}", redirectionUrl);
                throw HttpActionHelper.buildRedirectUrlAction(context, redirectionUrl);
            }

            // clean url from ticket parameter
            callbackUrl = substringBefore(callbackUrl, "?" + CasConfiguration.TICKET_PARAMETER + "=");
            callbackUrl = substringBefore(callbackUrl, "&" + CasConfiguration.TICKET_PARAMETER + "=");
            val casAuthenticator =
                new CasAuthenticator(configuration, getName(), urlResolver, callbackUrlResolver, callbackUrl);
            casAuthenticator.init();
            casAuthenticator.validate(credentials.get(), context, sessionStore);

            return credentials;
        } catch (CredentialsException e) {
            logger.error("Failed to retrieve or validate CAS credentials", e);
            return Optional.empty();
        }
    }

    @Override
    protected void defaultAuthenticator(final Authenticator authenticator) {
        throw new TechnicalException("You can not set an Authenticator for the DirectCasClient at startup. A new CasAuthenticator is "
            + "automatically created for each request");
    }
}
