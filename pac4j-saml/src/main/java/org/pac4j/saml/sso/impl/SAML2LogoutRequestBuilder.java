package org.pac4j.saml.sso.impl;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.sso.SAML2ObjectBuilder;
import org.pac4j.saml.util.Configuration;

/**
 * Build a SAML2 Logout Request
 * 
 * @author Matthieu Taggiasco
 * @since 1.9.2
 */

public class SAML2LogoutRequestBuilder implements SAML2ObjectBuilder<LogoutRequest> {

    private String bindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private int issueInstantSkewSeconds = 0;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();


    /**
     * Instantiates a new Saml 2 logout request builder.
     *
     * @param bindingType the binding type
     */
    public SAML2LogoutRequestBuilder(final String bindingType) {
        this.bindingType = bindingType;
    }

	@Override
	public LogoutRequest build(SAML2MessageContext context) {
        final SingleLogoutService ssoService = context.getIDPSingleLogoutService(this.bindingType);
        final AssertionConsumerService assertionConsumerService = context.getSPAssertionConsumerService();

        return buildLogoutRequest(context, assertionConsumerService, ssoService);
	}

    @SuppressWarnings("unchecked")
    protected final LogoutRequest buildLogoutRequest(final SAML2MessageContext context,
                                                     final AssertionConsumerService assertionConsumerService,
                                                     final SingleLogoutService ssoService) {
	
		final SAMLObjectBuilder<LogoutRequest> builder = (SAMLObjectBuilder<LogoutRequest>) this.builderFactory
		        .getBuilder(LogoutRequest.DEFAULT_ELEMENT_NAME);
		final LogoutRequest request = builder.buildObject();

		final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();

		request.setID(generateID());
		request.setIssuer(getIssuer(selfContext.getEntityId()));
		request.setIssueInstant(DateTime.now().plusSeconds(this.issueInstantSkewSeconds));
		request.setVersion(SAMLVersion.VERSION_20);
		request.setDestination(ssoService.getLocation());

		// very very bad...
		org.pac4j.core.context.WebContext ctx = context.getWebContext();
		org.pac4j.core.profile.ProfileManager manager = new org.pac4j.core.profile.ProfileManager(ctx);
		java.util.Optional<org.pac4j.core.profile.UserProfile> p = manager.get(true);
		if(p.isPresent() && p.get() instanceof org.pac4j.saml.profile.SAML2Profile) {
			final org.pac4j.saml.profile.SAML2Profile samlP = (org.pac4j.saml.profile.SAML2Profile) p.get();
			final String sessIdx = (String) samlP.getAttribute("sessionindex");
			SessionIndex sessionIdx = new SessionIndexBuilder().buildObject();
			sessionIdx.setSessionIndex(sessIdx);
			request.getSessionIndexes().add(sessionIdx);
		}

		return request;
	}

    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final String spEntityId) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
                .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        return issuer;
    }

    protected final String generateID() {
        return "_".concat(RandomStringUtils.randomAlphanumeric(39)).toLowerCase();
    }

    public void setIssueInstantSkewSeconds(final int issueInstantSkewSeconds) {
        this.issueInstantSkewSeconds = issueInstantSkewSeconds;
    }
}
