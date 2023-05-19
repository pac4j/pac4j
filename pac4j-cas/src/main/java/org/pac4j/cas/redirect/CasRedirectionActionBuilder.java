package org.pac4j.cas.redirect;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apereo.cas.client.Protocol;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.context.CallContext;
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

    /**
     * <p>Constructor for CasRedirectionActionBuilder.</p>
     *
     * @param configuration a {@link CasConfiguration} object
     * @param client a {@link CasClient} object
     */
    public CasRedirectionActionBuilder(final CasConfiguration configuration, final CasClient client) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("client", client);
        this.configuration = configuration;
        this.client = client;
    }

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getRedirectionAction(final CallContext ctx) {
        val webContext = ctx.webContext();

        var computeLoginUrl = configuration.computeFinalLoginUrl(webContext);
        val computedCallbackUrl = client.computeFinalCallbackUrl(webContext);

        val renew = configuration.isRenew()
            || webContext.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_FORCE_AUTHN).isPresent();
        val gateway = configuration.isGateway()
            || webContext.getRequestAttribute(RedirectionActionBuilder.ATTRIBUTE_PASSIVE).isPresent();
        val redirectionUrl = constructRedirectUrl(computeLoginUrl, getServiceParameter(),
                computedCallbackUrl, renew, gateway, configuration.getMethod());
        LOGGER.debug("redirectionUrl: {}", redirectionUrl);
        return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, redirectionUrl));
    }

    /**
     * <p>getServiceParameter.</p>
     *
     * @return a {@link String} object
     */
    protected String getServiceParameter() {
        if (configuration.getProtocol() == CasProtocol.SAML) {
            return Protocol.SAML11.getServiceParameterName();
        } else {
            return CasConfiguration.SERVICE_PARAMETER;
        }
    }

    // like CommonUtils.constructRedirectUrl in CAS client
    /**
     * <p>constructRedirectUrl.</p>
     *
     * @param casServerLoginUrl a {@link String} object
     * @param serviceParameterName a {@link String} object
     * @param serviceUrl a {@link String} object
     * @param renew a boolean
     * @param gateway a boolean
     * @param method a {@link String} object
     * @return a {@link String} object
     */
    public static String constructRedirectUrl(final String casServerLoginUrl, final String serviceParameterName,
                                              final String serviceUrl, final boolean renew, final boolean gateway, final String method) {
        return casServerLoginUrl + (casServerLoginUrl.contains("?") ? "&" : "?") + serviceParameterName + "="
            + CommonHelper.urlEncode(serviceUrl) + (renew ? "&renew=true" : "") + (gateway ? "&gateway=true" : "")
            + (method != null ? "&method=" + method : "");
    }
}
