package org.pac4j.cas.redirect;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.client.Protocol;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;

import java.util.Optional;

/**
 * CAS redirection action builder.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Slf4j
public class CasRedirectionActionBuilder implements RedirectionActionBuilder {

    protected CasConfiguration configuration;

    protected CasClient client;

    public CasRedirectionActionBuilder(final CasConfiguration configuration, final CasClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    @Override
    public Optional<RedirectionAction> getRedirectionAction(final WebContext context, final SessionStore sessionStore) {
        var computeLoginUrl = configuration.computeFinalLoginUrl(context);
        val computedCallbackUrl = client.computeFinalCallbackUrl(context);

        val renew = configuration.isRenew()
            || context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN).isPresent();
        val gateway = configuration.isGateway()
            || context.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE).isPresent();
        val redirectionUrl = constructRedirectUrl(computeLoginUrl, getServiceParameter(),
                computedCallbackUrl, renew, gateway, configuration.getMethod());
        LOGGER.debug("redirectionUrl: {}", redirectionUrl);
        return Optional.of(HttpActionHelper.buildRedirectUrlAction(context, redirectionUrl));
    }

    protected String getServiceParameter() {
        if (configuration.getProtocol() == CasProtocol.SAML) {
            return Protocol.SAML11.getServiceParameterName();
        } else {
            return CasConfiguration.SERVICE_PARAMETER;
        }
    }

    // like CommonUtils.constructRedirectUrl in CAS client
    public static String constructRedirectUrl(final String casServerLoginUrl, final String serviceParameterName,
                                              final String serviceUrl, final boolean renew, final boolean gateway, final String method) {
        return casServerLoginUrl + (casServerLoginUrl.contains("?") ? "&" : "?") + serviceParameterName + "="
            + CommonHelper.urlEncode(serviceUrl) + (renew ? "&renew=true" : "") + (gateway ? "&gateway=true" : "")
            + (method != null ? "&method=" + method : "");
    }
}
