package org.pac4j.cas.credentials.authenticator;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.profile.CasProfileDefinition;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.http.callback.CallbackUrlResolver;
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
public class CasAuthenticator extends ProfileDefinitionAware<CommonProfile> implements Authenticator<TokenCredentials> {

    private final static Logger logger = LoggerFactory.getLogger(CasAuthenticator.class);

    protected CasConfiguration configuration;

    protected String clientName;

    protected CallbackUrlResolver callbackUrlResolver;

    protected String callbackUrl;

    public CasAuthenticator(final CasConfiguration configuration, final String clientName, final CallbackUrlResolver callbackUrlResolver,
                            final String callbackUrl) {
        this.configuration = configuration;
        this.clientName = clientName;
        this.callbackUrlResolver = callbackUrlResolver;
        this.callbackUrl = callbackUrl;
    }

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("callbackUrlResolver", callbackUrlResolver);
        CommonHelper.assertNotBlank("clientName", clientName);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
        CommonHelper.assertNotNull("configuration", configuration);

        defaultProfileDefinition(new CasProfileDefinition());
    }

    @Override
    public void validate(final TokenCredentials credentials, final WebContext context) {
        init();

        final String ticket = credentials.getToken();
        try {
            final String finalCallbackUrl = callbackUrlResolver.compute(callbackUrl, clientName, context);
            final Assertion assertion = configuration.retrieveTicketValidator(context).validate(ticket, finalCallbackUrl);
            final AttributePrincipal principal = assertion.getPrincipal();
            logger.debug("principal: {}", principal);

            final String id = principal.getName();
            final Map<String, Object> newAttributes = new HashMap<>();
            // restore attributes
            final Map<String, Object> attributes = principal.getAttributes();
            if (attributes != null) {
                for (final Map.Entry<String, Object> entry : attributes.entrySet()){
                    final String key = entry.getKey();
                    final Object value = entry.getValue();
                    final Object restored = ProfileHelper.getInternalAttributeHandler().restore(value);
                    newAttributes.put(key, restored);
                }
            }

            final CommonProfile profile;
            // in case of CAS proxy, don't restore the profile, just build a CAS one
            if (configuration.getProxyReceptor() != null) {
                profile = getProfileDefinition().newProfile(principal, configuration.getProxyReceptor());
                profile.setId(ProfileHelper.sanitizeIdentifier(profile, id));
                getProfileDefinition().convertAndAdd(profile, newAttributes);
            } else {
                profile = ProfileHelper.restoreOrBuildProfile(getProfileDefinition(), id, newAttributes, principal,
                    configuration.getProxyReceptor());
            }
            logger.debug("profile returned by CAS: {}", profile);

            credentials.setUserProfile(profile);
        } catch (final TicketValidationException e) {
            String message = "cannot validate CAS ticket: " + ticket;
            throw new TechnicalException(message, e);
        }
    }
}
