package org.pac4j.saml.redirect;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.sso.SAML2ObjectBuilder;
import org.pac4j.saml.sso.impl.SAML2AuthnRequestBuilder;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

/**
 * Redirect action builder for SAML 2.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2RedirectActionBuilder implements RedirectActionBuilder {

    protected SAML2ObjectBuilder<AuthnRequest> saml2ObjectBuilder;

    private final SAML2Client client;

    public SAML2RedirectActionBuilder(final SAML2Client client) {
        CommonHelper.assertNotNull("client", client);
        this.client = client;
        final SAML2ClientConfiguration cfg = client.getConfiguration();
        this.saml2ObjectBuilder = new SAML2AuthnRequestBuilder(cfg);
    }

    @Override
    public RedirectAction redirect(final WebContext wc) {
        final SAML2MessageContext context = this.client.getContextProvider().buildContext(wc);
        final String relayState = this.client.getStateGenerator().generateState(wc);

        final AuthnRequest authnRequest = this.saml2ObjectBuilder.build(context);
        this.client.getProfileHandler().send(context, authnRequest, relayState);

        final Pac4jSAMLResponse adapter = context.getProfileRequestContextOutboundMessageTransportResponse();
        if (this.client.getConfiguration().getAuthnRequestBindingType().equalsIgnoreCase(SAMLConstants.SAML2_POST_BINDING_URI)) {
            final String content = adapter.getOutgoingContent();
            return RedirectAction.success(content);
        }
        final String location = adapter.getRedirectUrl();
        return RedirectAction.redirect(location);
    }
}
