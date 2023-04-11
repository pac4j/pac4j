package org.pac4j.cas.credentials.authenticator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.client.authentication.AttributePrincipal;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.TicketValidationException;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfileDefinition;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;

import java.util.HashMap;
import java.util.Optional;

/**
 * CAS authenticator which validates the service ticket.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Slf4j
public class CasAuthenticator extends ProfileDefinitionAware implements Authenticator {

    protected CasConfiguration configuration;

    protected String clientName;

    protected UrlResolver urlResolver;

    protected CallbackUrlResolver callbackUrlResolver;

    protected String callbackUrl;

    /**
     * <p>Constructor for CasAuthenticator.</p>
     *
     * @param configuration a {@link org.pac4j.cas.config.CasConfiguration} object
     * @param clientName a {@link java.lang.String} object
     * @param urlResolver a {@link org.pac4j.core.http.url.UrlResolver} object
     * @param callbackUrlResolver a {@link org.pac4j.core.http.callback.CallbackUrlResolver} object
     * @param callbackUrl a {@link java.lang.String} object
     */
    public CasAuthenticator(final CasConfiguration configuration, final String clientName, final UrlResolver urlResolver,
                            final CallbackUrlResolver callbackUrlResolver, final String callbackUrl) {
        this.configuration = configuration;
        this.clientName = clientName;
        this.urlResolver = urlResolver;
        this.callbackUrlResolver = callbackUrlResolver;
        this.callbackUrl = callbackUrl;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("urlResolver", urlResolver);
        CommonHelper.assertNotNull("callbackUrlResolver", callbackUrlResolver);
        CommonHelper.assertNotBlank("clientName", clientName);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotNull("configuration", configuration);

        setProfileDefinitionIfUndefined(new CasProfileDefinition());
    }

    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials cred) {
        if (cred instanceof TokenCredentials credentials) {
            init();

            val webContext = ctx.webContext();

            val ticket = credentials.getToken();
            try {
                val assertion = validateTicket(webContext, ticket);

                recordSession(ctx, ticket);

                val principal = assertion.getPrincipal();
                LOGGER.debug("principal: {}", principal);
                val profile = createUserProfile(principal);

                createUserProfileAttributes(assertion, principal, profile);
                LOGGER.debug("profile returned by CAS: {}", profile);

                credentials.setUserProfile(profile);
            } catch (final TicketValidationException e) {
                var message = "cannot validate CAS ticket: " + ticket;
                throw new TechnicalException(message, e);
            }
        }

        return Optional.ofNullable(cred);
    }

    protected void recordSession(final CallContext ctx, final String ticket) {
        configuration.findSessionLogoutHandler().recordSession(ctx, ticket);
    }

    protected void createUserProfileAttributes(final Assertion assertion, final AttributePrincipal principal,
                                               final UserProfile profile) {
        val newPrincipalAttributes = new HashMap<String, Object>();
        val newAuthenticationAttributes = new HashMap<String, Object>();
        // restore both sets of attributes
        val oldPrincipalAttributes = principal.getAttributes();
        val oldAuthenticationAttributes = assertion.getAttributes();
        if (oldPrincipalAttributes != null) {
            newPrincipalAttributes.putAll(oldPrincipalAttributes);
        }
        if (oldAuthenticationAttributes != null) {
            newAuthenticationAttributes.putAll(oldAuthenticationAttributes);
        }

        getProfileDefinition().convertAndAdd(profile, newPrincipalAttributes, newAuthenticationAttributes);
    }

    protected UserProfile createUserProfile(final AttributePrincipal principal) {
        val id = principal.getName();
        val profile = getProfileDefinition().newProfile(id, configuration.getProxyReceptor(), principal);
        profile.setId(ProfileHelper.sanitizeIdentifier(id));
        return profile;
    }

    protected Assertion validateTicket(final WebContext webContext, final String ticket) throws TicketValidationException {
        val finalCallbackUrl = callbackUrlResolver.compute(urlResolver, callbackUrl, clientName, webContext);
        return configuration.retrieveTicketValidator(webContext).validate(ticket, finalCallbackUrl);
    }
}
