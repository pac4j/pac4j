package org.pac4j.saml.logout;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.logout.LogoutActionBuilder;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAMLContextProvider;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestBuilder;
import org.pac4j.saml.logout.impl.SAML2LogoutRequestMessageSender;
import org.pac4j.saml.profile.SAML2Profile;

import java.util.Optional;

/**
 * Logout action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Slf4j
public class SAML2LogoutActionBuilder implements LogoutActionBuilder {

    protected SAML2LogoutRequestBuilder saml2LogoutRequestBuilder;

    protected final SAML2LogoutRequestMessageSender saml2LogoutRequestMessageSender;

    protected final SAMLContextProvider contextProvider;

    protected final SAML2Configuration configuration;

    protected final ValueGenerator stateGenerator;

    protected final SAML2Client saml2Client;

    /**
     * <p>Constructor for SAML2LogoutActionBuilder.</p>
     *
     * @param client a {@link SAML2Client} object
     */
    public SAML2LogoutActionBuilder(final SAML2Client client) {
        this.saml2Client = client;
        this.saml2LogoutRequestMessageSender = client.getLogoutRequestMessageSender();
        this.contextProvider = client.getContextProvider();
        this.configuration = client.getConfiguration();
        this.stateGenerator = client.getStateGenerator();
        this.saml2LogoutRequestBuilder = new SAML2LogoutRequestBuilder(configuration);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<RedirectionAction> getLogoutAction(final CallContext ctx, final UserProfile currentProfile, final String targetUrl) {
        try {
            if (currentProfile instanceof SAML2Profile saml2Profile) {
                val samlContext = this.contextProvider.buildContext(ctx, this.saml2Client);
                val relayState = this.stateGenerator.generateValue(ctx);

                val logoutRequest = this.saml2LogoutRequestBuilder.build(samlContext, saml2Profile);
                saml2LogoutRequestMessageSender.sendMessage(samlContext, logoutRequest, relayState);

                val webContext = ctx.webContext();

                val adapter = samlContext.getProfileRequestContextOutboundMessageTransportResponse();
                if (this.configuration.getSpLogoutRequestBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
                    val content = adapter.getOutgoingContent();
                    return Optional.of(HttpActionHelper.buildFormPostContentAction(webContext, content));
                }
                val location = adapter.getRedirectUrl();
                return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, location));
            }
        } catch (final Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(e.getMessage(), e);
            } else {
                LOGGER.warn(e.getMessage());
            }
        }
        return Optional.empty();
    }
}
