package org.pac4j.cas.credentials.authenticator;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfileDefinition;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
import org.pac4j.core.http.url.UrlResolver;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.definition.ProfileDefinitionAware;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * CAS authenticator which validates the service ticket.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasAuthenticator extends ProfileDefinitionAware<CommonProfile> implements Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(CasAuthenticator.class);

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
    protected void internalInit() {
        CommonHelper.assertNotNull("urlResolver", urlResolver);
        CommonHelper.assertNotNull("callbackUrlResolver", callbackUrlResolver);
        CommonHelper.assertNotBlank("clientName", clientName);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotNull("configuration", configuration);

        defaultProfileDefinition(new CasProfileDefinition());
    }

    @Override
    public void validate(final Credentials cred, final WebContext context) {
        init();

        final TokenCredentials credentials = (TokenCredentials) cred;
        final String ticket = credentials.getToken();
        try {
            final String finalCallbackUrl = callbackUrlResolver.compute(urlResolver, callbackUrl, clientName, context);
            final Assertion assertion = configuration.retrieveTicketValidator(context).validate(ticket, finalCallbackUrl);
            final AttributePrincipal principal = assertion.getPrincipal();
            logger.debug("principal: {}", principal);

            final String id = principal.getName();
            final Map<String, Object> newPrincipalAttributes = new HashMap<>();
            final Map<String, Object> newAuthenticationAttributes = new HashMap<>();
            // restore both sets of attributes
            final Map<String, Object> oldPrincipalAttributes = principal.getAttributes();
            final Map<String, Object> oldAuthenticationAttributes = assertion.getAttributes();
            if (oldPrincipalAttributes != null) {
                oldPrincipalAttributes.entrySet().stream()
                    .forEach(e -> newPrincipalAttributes.put(e.getKey(), e.getValue()));
            }
            if (oldAuthenticationAttributes != null) {
                oldAuthenticationAttributes.entrySet().stream()
                    .forEach(e -> newAuthenticationAttributes.put(e.getKey(), e.getValue()));
            }

            final CommonProfile profile;
            // in case of CAS proxy, don't restore the profile, just build a CAS one
            if (configuration.getProxyReceptor() != null) {
                profile = getProfileDefinition().newProfile(principal, configuration.getProxyReceptor());
                profile.setId(ProfileHelper.sanitizeIdentifier(profile, id));
                getProfileDefinition().convertAndAdd(profile, newPrincipalAttributes, newAuthenticationAttributes);
            } else {
                profile = ProfileHelper.restoreOrBuildProfile(getProfileDefinition(), id, newPrincipalAttributes,
                        newAuthenticationAttributes, principal, configuration.getProxyReceptor());
            }
            logger.debug("profile returned by CAS: {}", profile);

            credentials.setUserProfile(profile);
        } catch (final TicketValidationException e) {
            String message = "cannot validate CAS ticket: " + ticket;
            throw new TechnicalException(message, e);
        }
    }
}
