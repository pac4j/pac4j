package org.pac4j.saml.sso.impl;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.api.SAML2ObjectBuilder;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;

/**
 * Build a SAML2 Authn Request from the given {@link MessageContext}.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.5.0
 */
public class SAML2AuthnRequestBuilder implements SAML2ObjectBuilder<AuthnRequest> {
    private final SAML2Configuration configuration;

    private int issueInstantSkewSeconds = 0;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    /**
     * Instantiates a new Saml 2 authn request builder.
     *
     * @param cfg Client configuration.
     */
    public SAML2AuthnRequestBuilder(final SAML2Configuration cfg) {
        this.configuration = cfg;
    }

    @Override
    public AuthnRequest build(final SAML2MessageContext context) {
        final SingleSignOnService ssoService = context.getIDPSingleSignOnService(this.configuration.getAuthnRequestBindingType());
        final String idx = this.configuration.getAssertionConsumerServiceIndex() > 0
            ? String.valueOf(this.configuration.getAssertionConsumerServiceIndex())
            : null;
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

        final AuthnContextComparisonTypeEnumeration comparisonType = getComparisonTypeEnumFromString(configuration.getComparisonType());
        if (comparisonType != null) {
            final RequestedAuthnContext authnContext = new RequestedAuthnContextBuilder().buildObject();
            authnContext.setComparison(comparisonType);

            if (this.configuration.getAuthnContextClassRefs() != null && !this.configuration.getAuthnContextClassRefs().isEmpty()) {
                final List<AuthnContextClassRef> refs = authnContext.getAuthnContextClassRefs();
                this.configuration.getAuthnContextClassRefs().forEach(r -> refs.add(buildAuthnContextClassRef(r)));
            }
            request.setRequestedAuthnContext(authnContext);
        }

        final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();

        request.setID(SAML2Utils.generateID());
        request.setIssuer(getIssuer(selfContext.getEntityId()));
        request.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(this.issueInstantSkewSeconds).toInstant());
        request.setVersion(SAMLVersion.VERSION_20);
        request.setIsPassive(this.configuration.isPassive());
        request.setForceAuthn(this.configuration.isForceAuth());

        if (StringUtils.isNotBlank(this.configuration.getProviderName())) {
            request.setProviderName(this.configuration.getProviderName());
        }

        if (this.configuration.getNameIdPolicyFormat() != null) {
            final NameIDPolicy nameIdPolicy = new NameIDPolicyBuilder().buildObject();
            if (this.configuration.isNameIdPolicyAllowCreate() != null) {
                nameIdPolicy.setAllowCreate(this.configuration.isNameIdPolicyAllowCreate());
            }
            nameIdPolicy.setFormat(this.configuration.getNameIdPolicyFormat());
            request.setNameIDPolicy(nameIdPolicy);
        }

        request.setDestination(ssoService.getLocation());
        if (this.configuration.getAssertionConsumerServiceIndex()  >= 0) {
            request.setAssertionConsumerServiceIndex(this.configuration.getAssertionConsumerServiceIndex() );
        } else {
            request.setAssertionConsumerServiceURL(assertionConsumerService.getLocation());
            request.setProtocolBinding(assertionConsumerService.getBinding());
        }
        

        if (this.configuration.getAttributeConsumingServiceIndex() >= 0) {
            request.setAttributeConsumingServiceIndex(this.configuration.getAttributeConsumingServiceIndex());
        }

        // Setting extensions if they are defined
        if (this.configuration.getAuthnRequestExtensions() != null) {
            final Extensions extensionsElem = ((SAMLObjectBuilder<Extensions>) this.builderFactory
                .getBuilder(Extensions.DEFAULT_ELEMENT_NAME)).buildObject();
            extensionsElem.getUnknownXMLObjects().addAll(this.configuration.getAuthnRequestExtensions() .get());
            request.setExtensions(extensionsElem);
        }

        return request;
    }

    protected AuthnContextClassRef buildAuthnContextClassRef(final String authnContextClassRef) {
        final AuthnContextClassRef classRef = new AuthnContextClassRefBuilder().buildObject();
        classRef.setURI(authnContextClassRef);
        return classRef;
    }

    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final String spEntityId) {
        final SAMLObjectBuilder<Issuer> issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
            .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        final Issuer issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        final String issuerFormat = this.configuration.getIssuerFormat();
        if (issuerFormat != null) {
            issuer.setFormat(issuerFormat);
        }
        if (this.configuration.isUseNameQualifier()) {
            issuer.setNameQualifier(spEntityId);
        }
        return issuer;
    }

    protected AuthnContextComparisonTypeEnumeration getComparisonTypeEnumFromString(final String comparisonType) {
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
