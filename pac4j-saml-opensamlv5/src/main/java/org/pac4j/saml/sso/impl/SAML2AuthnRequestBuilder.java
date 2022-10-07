package org.pac4j.saml.sso.impl;

import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.IDPEntry;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.profile.api.SAML2ObjectBuilder;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Build a SAML2 Authn Request from the given {@link MessageContext}.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.5.0
 */
public class SAML2AuthnRequestBuilder implements SAML2ObjectBuilder<AuthnRequest> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private int issueInstantSkewSeconds = 0;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    @Override
    public AuthnRequest build(final SAML2MessageContext context) {
        final var configContext = context.getConfigurationContext();

        final var ssoService = context.getIDPSingleSignOnService(configContext.getAuthnRequestBindingType());
        final var idx = configContext.getAssertionConsumerServiceIndex() > 0
            ? String.valueOf(configContext.getAssertionConsumerServiceIndex())
            : null;
        final var assertionConsumerService = context.getSPAssertionConsumerService(idx);
        final var authnRequest = buildAuthnRequest(context, assertionConsumerService, ssoService);
        SAML2Utils.logProtocolMessage(authnRequest);
        return authnRequest;
    }

    @SuppressWarnings("unchecked")
    protected final AuthnRequest buildAuthnRequest(final SAML2MessageContext context,
                                                   final AssertionConsumerService assertionConsumerService,
                                                   final SingleSignOnService ssoService) {
        final var configContext = context.getConfigurationContext();
        final var builder = (SAMLObjectBuilder<AuthnRequest>) this.builderFactory
            .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        final var request = builder.buildObject();

        final var comparisonType = getComparisonTypeEnumFromString(configContext.getComparisonType());
        if (comparisonType != null) {
            final var authnContext = new RequestedAuthnContextBuilder().buildObject();
            authnContext.setComparison(comparisonType);

            if (configContext.getAuthnContextClassRefs() != null && !configContext.getAuthnContextClassRefs().isEmpty()) {
                final var refs = authnContext.getAuthnContextClassRefs();
                configContext.getAuthnContextClassRefs().forEach(r -> refs.add(buildAuthnContextClassRef(r)));
            }
            request.setRequestedAuthnContext(authnContext);
        }

        final var selfContext = context.getSAMLSelfEntityContext();

        request.setID(SAML2Utils.generateID());
        request.setIssuer(getIssuer(context, selfContext.getEntityId()));
        request.setIssueInstant(ZonedDateTime.now(ZoneOffset.UTC).plusSeconds(this.issueInstantSkewSeconds).toInstant());
        request.setVersion(SAMLVersion.VERSION_20);

        request.setIsPassive(configContext.isPassive());
        request.setForceAuthn(configContext.isForceAuth());

        if (CommonHelper.isNotBlank(configContext.getProviderName())) {
            request.setProviderName(configContext.getProviderName());
        }

        if (configContext.getNameIdPolicyFormat() != null) {
            final var nameIdPolicy = new NameIDPolicyBuilder().buildObject();
            if (configContext.isNameIdPolicyAllowCreate() != null) {
                nameIdPolicy.setAllowCreate(configContext.isNameIdPolicyAllowCreate());
            }
            nameIdPolicy.setFormat(configContext.getNameIdPolicyFormat());
            request.setNameIDPolicy(nameIdPolicy);
        }

        request.setDestination(ssoService.getLocation());
        if (configContext.getAssertionConsumerServiceIndex()  >= 0) {
            request.setAssertionConsumerServiceIndex(configContext.getAssertionConsumerServiceIndex());
        } else {
            request.setAssertionConsumerServiceURL(assertionConsumerService.getLocation());
            request.setProtocolBinding(assertionConsumerService.getBinding());
        }

        if (configContext.getAttributeConsumingServiceIndex() >= 0) {
            request.setAttributeConsumingServiceIndex(configContext.getAttributeConsumingServiceIndex());
        }

        final var extensions = ((SAMLObjectBuilder<Extensions>) this.builderFactory
            .getBuilder(Extensions.DEFAULT_ELEMENT_NAME)).buildObject();

        if (!configContext.getSAML2Configuration().getRequestedServiceProviderAttributes().isEmpty()) {
            final var attrBuilder =
                (SAMLObjectBuilder<RequestedAttribute>) this.builderFactory.getBuilder(RequestedAttribute.DEFAULT_ELEMENT_NAME);
            configContext.getSAML2Configuration().getRequestedServiceProviderAttributes().forEach(attribute -> {
                final var requestAttribute = attrBuilder.buildObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
                requestAttribute.setIsRequired(attribute.isRequired());
                requestAttribute.setName(attribute.getName());
                requestAttribute.setFriendlyName(attribute.getFriendlyName());
                requestAttribute.setNameFormat(attribute.getNameFormat());
                extensions.getUnknownXMLObjects().add(requestAttribute);
            });
        }

        // Setting extensions if they are defined
        if (configContext.getSAML2Configuration().getAuthnRequestExtensions() != null) {
            extensions.getUnknownXMLObjects().addAll(configContext.getSAML2Configuration().getAuthnRequestExtensions() .get());
        }

        if (!extensions.getUnknownXMLObjects().isEmpty()) {
            request.setExtensions(extensions);
        }

        final var givenIdPs = configContext.getSAML2Configuration().getScopingIdentityProviders();
        if (!givenIdPs.isEmpty()) {
            final var scopingBuilder = (SAMLObjectBuilder<Scoping>) this.builderFactory
                .getBuilder(Scoping.DEFAULT_ELEMENT_NAME);
            final var scoping = scopingBuilder.buildObject();
            final var idpEntryBuilder = (SAMLObjectBuilder<IDPEntry>) this.builderFactory
                .getBuilder(IDPEntry.DEFAULT_ELEMENT_NAME);

            final var idpListBuilder = (SAMLObjectBuilder<IDPList>) this.builderFactory
                .getBuilder(IDPList.DEFAULT_ELEMENT_NAME);
            scoping.setIDPList(idpListBuilder.buildObject());

            givenIdPs.forEach(idp -> {
                final var idpEntry = idpEntryBuilder.buildObject();
                idpEntry.setProviderID(idp.getProviderId());
                idpEntry.setName(idp.getName());
                scoping.getIDPList().getIDPEntrys().add(idpEntry);
            });

            if (!scoping.getIDPList().getIDPEntrys().isEmpty()) {
                request.setScoping(scoping);
            }
        }
        return request;
    }

    protected AuthnContextClassRef buildAuthnContextClassRef(final String authnContextClassRef) {
        final var classRef = new AuthnContextClassRefBuilder().buildObject();
        classRef.setURI(authnContextClassRef);
        return classRef;
    }

    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final SAML2MessageContext context, final String spEntityId) {
        final var configContext = context.getConfigurationContext();

        final var issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
            .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        final var issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        final var issuerFormat = configContext.getIssuerFormat();
        if (issuerFormat != null) {
            issuer.setFormat(issuerFormat);
        }
        if (configContext.isUseNameQualifier()) {
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
