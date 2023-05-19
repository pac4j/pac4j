package org.pac4j.saml.sso.impl;

import lombok.val;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
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
 * Build a SAML2 Authn Request from the given {@link org.opensaml.messaging.context.MessageContext}.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.5.0
 */
public class SAML2AuthnRequestBuilder implements SAML2ObjectBuilder<AuthnRequest> {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private int issueInstantSkewSeconds = 0;

    private final XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();

    /** {@inheritDoc} */
    @Override
    public AuthnRequest build(final SAML2MessageContext context) {
        val configContext = context.getConfigurationContext();

        val ssoService = context.getIDPSingleSignOnService(configContext.getAuthnRequestBindingType());
        val idx = configContext.getAssertionConsumerServiceIndex() > 0
            ? String.valueOf(configContext.getAssertionConsumerServiceIndex())
            : null;
        val assertionConsumerService = context.getSPAssertionConsumerService(idx);
        val authnRequest = buildAuthnRequest(context, assertionConsumerService, ssoService);
        SAML2Utils.logProtocolMessage(authnRequest);
        return authnRequest;
    }

    /**
     * <p>buildAuthnRequest.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     * @param assertionConsumerService a {@link org.opensaml.saml.saml2.metadata.AssertionConsumerService} object
     * @param ssoService a {@link org.opensaml.saml.saml2.metadata.SingleSignOnService} object
     * @return a {@link AuthnRequest} object
     */
    @SuppressWarnings("unchecked")
    protected final AuthnRequest buildAuthnRequest(final SAML2MessageContext context,
                                                   final Endpoint assertionConsumerService,
                                                   final Endpoint ssoService) {
        val configContext = context.getConfigurationContext();
        val builder = (SAMLObjectBuilder<AuthnRequest>) this.builderFactory
            .getBuilder(AuthnRequest.DEFAULT_ELEMENT_NAME);
        val request = builder.buildObject();

        val comparisonType = getComparisonTypeEnumFromString(configContext.getComparisonType());
        if (comparisonType != null) {
            val authnContext = new RequestedAuthnContextBuilder().buildObject();
            authnContext.setComparison(comparisonType);

            if (configContext.getAuthnContextClassRefs() != null && !configContext.getAuthnContextClassRefs().isEmpty()) {
                val refs = authnContext.getAuthnContextClassRefs();
                configContext.getAuthnContextClassRefs().forEach(r -> refs.add(buildAuthnContextClassRef(r)));
            }
            request.setRequestedAuthnContext(authnContext);
        }

        val selfContext = context.getSAMLSelfEntityContext();

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
            val nameIdPolicy = new NameIDPolicyBuilder().buildObject();
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

        val extensions = ((SAMLObjectBuilder<Extensions>) this.builderFactory
            .getBuilder(Extensions.DEFAULT_ELEMENT_NAME)).buildObject();

        if (!configContext.getSAML2Configuration().getRequestedServiceProviderAttributes().isEmpty()) {
            val attrBuilder =
                (SAMLObjectBuilder<RequestedAttribute>) this.builderFactory.getBuilder(RequestedAttribute.DEFAULT_ELEMENT_NAME);
            configContext.getSAML2Configuration().getRequestedServiceProviderAttributes().forEach(attribute -> {
                val requestAttribute = attrBuilder.buildObject(RequestedAttribute.DEFAULT_ELEMENT_NAME);
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

        val givenIdPs = configContext.getSAML2Configuration().getScopingIdentityProviders();
        if (!givenIdPs.isEmpty()) {
            val scopingBuilder = (SAMLObjectBuilder<Scoping>) this.builderFactory
                .getBuilder(Scoping.DEFAULT_ELEMENT_NAME);
            val scoping = scopingBuilder.buildObject();
            val idpEntryBuilder = (SAMLObjectBuilder<IDPEntry>) this.builderFactory
                .getBuilder(IDPEntry.DEFAULT_ELEMENT_NAME);

            val idpListBuilder = (SAMLObjectBuilder<IDPList>) this.builderFactory
                .getBuilder(IDPList.DEFAULT_ELEMENT_NAME);
            scoping.setIDPList(idpListBuilder.buildObject());

            givenIdPs.forEach(idp -> {
                val idpEntry = idpEntryBuilder.buildObject();
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

    /**
     * <p>buildAuthnContextClassRef.</p>
     *
     * @param authnContextClassRef a {@link String} object
     * @return a {@link AuthnContextClassRef} object
     */
    protected AuthnContextClassRef buildAuthnContextClassRef(final String authnContextClassRef) {
        val classRef = new AuthnContextClassRefBuilder().buildObject();
        classRef.setURI(authnContextClassRef);
        return classRef;
    }

    /**
     * <p>getIssuer.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     * @param spEntityId a {@link String} object
     * @return a {@link Issuer} object
     */
    @SuppressWarnings("unchecked")
    protected final Issuer getIssuer(final SAML2MessageContext context, final String spEntityId) {
        val configContext = context.getConfigurationContext();

        val issuerBuilder = (SAMLObjectBuilder<Issuer>) this.builderFactory
            .getBuilder(Issuer.DEFAULT_ELEMENT_NAME);
        val issuer = issuerBuilder.buildObject();
        issuer.setValue(spEntityId);
        val issuerFormat = configContext.getIssuerFormat();
        if (issuerFormat != null) {
            issuer.setFormat(issuerFormat);
        }
        if (configContext.isUseNameQualifier()) {
            issuer.setNameQualifier(spEntityId);
        }
        return issuer;
    }

    /**
     * <p>getComparisonTypeEnumFromString.</p>
     *
     * @param comparisonType a {@link String} object
     * @return a {@link AuthnContextComparisonTypeEnumeration} object
     */
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

    /**
     * <p>Setter for the field <code>issueInstantSkewSeconds</code>.</p>
     *
     * @param issueInstantSkewSeconds a int
     */
    public void setIssueInstantSkewSeconds(final int issueInstantSkewSeconds) {
        this.issueInstantSkewSeconds = issueInstantSkewSeconds;
    }
}
