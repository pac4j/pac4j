package org.pac4j.cas.credentials.authenticator;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.client.validation.TicketValidationException;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfileDefinition;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.ProfileHelper;
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

    public CasAuthenticator(final CasConfiguration configuration, final String clientName, final UrlResolver urlResolver,
                            final CallbackUrlResolver callbackUrlResolver, final String callbackUrl) {
        this.configuration = configuration;
        this.clientName = clientName;
        this.urlResolver = urlResolver;
        this.callbackUrlResolver = callbackUrlResolver;
        this.callbackUrl = callbackUrl;
    }

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
        init();

        val webContext = ctx.webContext();

        val credentials = (TokenCredentials) cred;
        val ticket = credentials.getToken();
        try {
            val finalCallbackUrl = callbackUrlResolver.compute(urlResolver, callbackUrl, clientName, webContext);
            val assertion = configuration.retrieveTicketValidator(webContext).validate(ticket, finalCallbackUrl);
            val principal = assertion.getPrincipal();
            LOGGER.debug("principal: {}", principal);

            val id = principal.getName();
            val newPrincipalAttributes = new HashMap<String, Object>();
            val newAuthenticationAttributes = new HashMap<String, Object>();
            // restore both sets of attributes
            val oldPrincipalAttributes = principal.getAttributes();
            val oldAuthenticationAttributes = assertion.getAttributes();
            if (oldPrincipalAttributes != null) {
                oldPrincipalAttributes.entrySet().stream()
                    .forEach(e -> newPrincipalAttributes.put(e.getKey(), e.getValue()));
            }
            if (oldAuthenticationAttributes != null) {
                oldAuthenticationAttributes.entrySet().stream()
                    .forEach(e -> newAuthenticationAttributes.put(e.getKey(), e.getValue()));
            }

            val profile = getProfileDefinition().newProfile(id, configuration.getProxyReceptor(), principal);
            profile.setId(ProfileHelper.sanitizeIdentifier(id));
            getProfileDefinition().convertAndAdd(profile, newPrincipalAttributes, newAuthenticationAttributes);
            LOGGER.debug("profile returned by CAS: {}", profile);

            credentials.setUserProfile(profile);
        } catch (final TicketValidationException e) {
            var message = "cannot validate CAS ticket: " + ticket;
            throw new TechnicalException(message, e);
        }

        return Optional.of(credentials);
    }
}
