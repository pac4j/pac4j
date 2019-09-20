
package org.pac4j.saml.sso.impl;

import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.sso.SAML2ObjectBuilder;
import org.pac4j.saml.util.Configuration;

/**
 * Build a SAML2 Authn Request from the given {@link MessageContext}.
 *
 * @author Michael Remond
 * @since 1.5.0
 */

public class SAML2AuthnRequestBuilder implements SAML2ObjectBuilder<AuthnRequest> {

    private final boolean forceAuth;
    private final boolean passive;

    private final AuthnContextComparisonTypeEnumeration comparisonType;

    private String bindingType = SAMLConstants.SAML2_POST_BINDING_URI;

    private String authnContextClassRef = null;

    private String nameIdPolicyFormat = null;

    private int issueInstantSkewSeconds = 0;

    private final int attributeConsumingServiceIndex;

    private final int assertionConsumerServiceIndex;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    /**
     * Instantiates a new Saml 2 authn request builder.
     *
     * @param cfg Client configuration.
     */
    public SAML2AuthnRequestBuilder(final SAML2ClientConfiguration cfg) {
        this.forceAuth = cfg.isForceAuth();
        this.comparisonType = getComparisonTypeEnumFromString(cfg.getComparisonType());
        this.bindingType = cfg.getDestinationBindingType();
        this.authnContextClassRef = cfg.getAuthnContextClassRef();
        this.nameIdPolicyFormat = cfg.getNameIdPolicyFormat();
        this.passive = cfg.isPassive();
        this.attributeConsumingServiceIndex = cfg.getAttributeConsumingServiceIndex();
        this.assertionConsumerServiceIndex = cfg.getAssertionConsumerServiceIndex();
    }

    @Override
    public AuthnRequest build(final SAML2MessageContext context) {
        final SingleSignOnService ssoService = context.getIDPSingleSignOnService(this.bindingType);
        final String idx = this.assertionConsumerServiceIndex > 0 ? String.valueOf(assertionConsumerServiceIndex) : null;
        final AssertionConsumerService assertionConsumerService = context.getSPAssertionConsumerService(idx);
        return buildAuthnRequest(context, assertionConsumerService, ssoService);
    }

    @SuppressWarnings("unchecked")
    protected final AuthnRequest buildAuthnRequest(final SAML2MessageContext context,
                                                   final AssertionConsumerService assertionConsumerService,
                                                   final SingleSignOnService ssoService) {

        final SAMLObjectBuilder<AuthnRequest> builder = (SAMLObjectBuilder<AuthnRequest>) this.builderFactory
            .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        final AuthnRequest request = builder.buildObject();
        if (comparisonType != null) {
            final RequestedAuthnContext authnContext = new RequestedAuthnContextBuilder().buildObject();
            authnContext.setComparison(comparisonType);

            if (authnContextClassRef != null) {
                final AuthnContextClassRef classRef = new AuthnContextClassRefBuilder().buildObject();
                classRef.setAuthnContextClassRef(authnContextClassRef);
                authnContext.getAuthnContextClassRefs().add(classRef);
            }
            request.setRequestedAuthnContext(authnContext);
        }

        final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();

        request.setID(generateID());
        request.setIssuer(getIssuer(selfContext.getEntityId()));
        request.setIssueInstant(DateTime.now().plusSeconds(this.issueInstantSkewSeconds));
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIsPassive(this.passive);
        request.setForceAuthn(this.forceAuth);
        request.setProviderName("pac4j-saml");

        if (nameIdPolicyFormat != null) {
            final NameIDPolicy nameIdPolicy = new NameIDPolicyBuilder().buildObject();
            nameIdPolicy.setAllowCreate(true);
            nameIdPolicy.setFormat(nameIdPolicyFormat);
            request.setNameIDPolicy(nameIdPolicy);
        }

        request.setDestination(ssoService.getLocation());
        if (assertionConsumerServiceIndex >= 0) {
            request.setAssertionConsumerServiceIndex(assertionConsumerServiceIndex);
        } else {
            request.setAssertionConsumerServiceURL(assertionConsumerService.getLocation());
        }
        request.setProtocolBinding(assertionConsumerService.getBinding());

        if (attributeConsumingServiceIndex >= 0) {
            request.setAttributeConsumingServiceIndex(attributeConsumingServiceIndex);
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
        return "_".concat(CommonHelper.randomString(39)).toLowerCase();
    }

    protected final AuthnContextComparisonTypeEnumeration getComparisonTypeEnumFromString(final String comparisonType) {
        if ("exact".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.EXACT;
        }
        if ("minimum".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.MINIMUM;
        }
        if ("maximum".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.MAXIMUM;
        }
        if ("better".equalsIgnoreCase(comparisonType)) {
            return AuthnContextComparisonTypeEnumeration.BETTER;
        }
        return null;
    }

    public void setIssueInstantSkewSeconds(final int issueInstantSkewSeconds) {
        this.issueInstantSkewSeconds = issueInstantSkewSeconds;
    }
}
