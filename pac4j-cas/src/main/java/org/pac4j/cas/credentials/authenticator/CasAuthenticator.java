package org.pac4j.cas.credentials.authenticator;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.CasCredentials;
import org.pac4j.cas.profile.CasProfile;
import org.pac4j.cas.profile.CasProxyProfile;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CAS authenticator which validates the service ticket.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasAuthenticator implements Authenticator<CasCredentials> {

    private final static Logger logger = LoggerFactory.getLogger(CasAuthenticator.class);

    private final CasConfiguration configuration;

    private final String callbackUrl;

    public CasAuthenticator(final CasConfiguration configuration, final String callbackUrl) {
        this.configuration = configuration;
        this.callbackUrl = callbackUrl;
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotBlank("callbackUrl", callbackUrl);
    }

    @Override
    public void validate(final CasCredentials credentials, final WebContext context) throws HttpAction {
        final String ticket = credentials.getServiceTicket();
        try {
            final Assertion assertion = configuration.getTicketValidator().validate(ticket, configuration.getCallbackUrlResolver().compute(callbackUrl, context));
            final AttributePrincipal principal = assertion.getPrincipal();
            logger.debug("principal: {}", principal);
            final CasProfile casProfile;
            if (configuration.getProxyReceptor() != null) {
                casProfile = new CasProxyProfile();
                ((CasProxyProfile) casProfile).setPrincipal(principal);
            } else {
                casProfile = new CasProfile();
            }
            casProfile.setId(principal.getName());
            casProfile.addAttributes(principal.getAttributes());
            logger.debug("casProfile: {}", casProfile);
            credentials.setUserProfile(casProfile);
        } catch (final TicketValidationException e) {
            String message = "cannot validate CAS ticket: " + ticket;
            throw new TechnicalException(message, e);
        }
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public CasConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "configuration", configuration, "callbackUrl", callbackUrl);
    }
}
