package org.pac4j.saml.context;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.*;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.metadata.*;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.store.SAMLMessageStore;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Allow to store additional information for SAML processing.
 *
 * @author Michael Remond
 * @version 1.5.0
 */
@Getter
@Setter
@ToString
public class SAML2MessageContext {

    /**
     * SubjectConfirmations used during assertion evaluation.
     */
    private final List<SubjectConfirmation> subjectConfirmations = new ArrayList<>();

    private MessageContext messageContext = new MessageContext();

    private SAML2Configuration saml2Configuration;

    private final CallContext callContext;

    /* valid subject assertion */
    private Assertion subjectAssertion;

    /**
     * BaseID retrieved either from the Subject or from a SubjectConfirmation
     */
    private BaseID baseID;

    private SAMLMessageStore samlMessageStore;

    /**
     * <p>Constructor for SAML2MessageContext.</p>
     *
     * @param callContext a {@link CallContext} object
     */
    public SAML2MessageContext(final CallContext callContext) {
        this.callContext = callContext;
    }

    /**
     * <p>getConfigurationContext.</p>
     *
     * @return a {@link SAML2ConfigurationContext} object
     */
    public SAML2ConfigurationContext getConfigurationContext() {
        val webContext = callContext.webContext();
        CommonHelper.assertNotNull("webContext", webContext);
        CommonHelper.assertNotNull("saml2Configuration", this.saml2Configuration);
        return new SAML2ConfigurationContext(webContext, this.saml2Configuration);
    }

    /**
     * <p>getSPSSODescriptor.</p>
     *
     * @return a {@link SPSSODescriptor} object
     */
    public final SPSSODescriptor getSPSSODescriptor() {
        val selfContext = getSAMLSelfMetadataContext();
        val spDescriptor = (SPSSODescriptor) selfContext.getRoleDescriptor();
        return spDescriptor;
    }

    /**
     * <p>getIDPSSODescriptor.</p>
     *
     * @return a {@link IDPSSODescriptor} object
     */
    public final IDPSSODescriptor getIDPSSODescriptor() {
        val peerContext = getSAMLPeerMetadataContext();
        val idpssoDescriptor = (IDPSSODescriptor) peerContext.getRoleDescriptor();
        return idpssoDescriptor;
    }

    /**
     * <p>getIDPSingleLogoutService.</p>
     *
     * @param binding a {@link String} object
     * @return a {@link SingleLogoutService} object
     */
    public final SingleLogoutService getIDPSingleLogoutService(final String binding) {
        val services = getIDPSSODescriptor().getSingleLogoutServices();
        for (val service : services) {
            if (service.getBinding().equals(binding)) {
                return service;
            }
        }
        throw new SAMLException("Identity provider has no single logout service available for the selected profile "
            + binding);
    }

    /**
     * <p>getIDPSingleSignOnService.</p>
     *
     * @param binding a {@link String} object
     * @return a {@link SingleSignOnService} object
     */
    public SingleSignOnService getIDPSingleSignOnService(final String binding) {
        val services = getIDPSSODescriptor().getSingleSignOnServices();
        for (val service : services) {
            if (service.getBinding().equals(binding)) {
                return service;
            }
        }
        throw new SAMLException("Identity provider has no single sign on service available for the selected profile "
            + binding);
    }

    /**
     * <p>getSPAssertionConsumerService.</p>
     *
     * @return a {@link AssertionConsumerService} object
     */
    public AssertionConsumerService getSPAssertionConsumerService() {
        val spssoDescriptor = getSPSSODescriptor();
        return getSPAssertionConsumerService(spssoDescriptor, spssoDescriptor.getAssertionConsumerServices());
    }

    /**
     * <p>getSPAssertionConsumerService.</p>
     *
     * @param response a {@link StatusResponseType} object
     * @return a {@link AssertionConsumerService} object
     */
    public AssertionConsumerService getSPAssertionConsumerService(final StatusResponseType response) {
        val spssoDescriptor = getSPSSODescriptor();
        val services = spssoDescriptor.getAssertionConsumerServices();

        // Get by index
        if (response != null && StringUtils.isNotEmpty(response.getDestination())) {
            for (val service : services) {
                if (response.getDestination().equals(service.getLocation())) {
                    return service;
                }
            }
            throw new SAMLException("Assertion consumer service with destination " + response.getDestination()
                + " could not be found for spDescriptor " + spssoDescriptor);
        }

        return getSPAssertionConsumerService(spssoDescriptor, services);
    }

    /**
     * <p>getSPAssertionConsumerService.</p>
     *
     * @param acsIndex a {@link String} object
     * @return a {@link AssertionConsumerService} object
     */
    public AssertionConsumerService getSPAssertionConsumerService(final String acsIndex) {
        val spssoDescriptor = getSPSSODescriptor();
        val services = spssoDescriptor.getAssertionConsumerServices();

        // Get by index
        if (acsIndex != null) {
            for (val service : services) {
                if (Integer.valueOf(acsIndex).equals(service.getIndex())) {
                    return service;
                }
            }
            throw new SAMLException("Assertion consumer service with index " + acsIndex
                + " could not be found for spDescriptor " + spssoDescriptor);
        }

        return getSPAssertionConsumerService(spssoDescriptor, services);
    }

    /**
     * <p>getSPAssertionConsumerService.</p>
     *
     * @param spssoDescriptor a {@link SPSSODescriptor} object
     * @param services a {@link List} object
     * @return a {@link AssertionConsumerService} object
     */
    protected AssertionConsumerService getSPAssertionConsumerService(
        final SPSSODescriptor spssoDescriptor,
        final Collection<AssertionConsumerService> services) {

        // Get default
        if (spssoDescriptor.getDefaultAssertionConsumerService() != null) {
            return spssoDescriptor.getDefaultAssertionConsumerService();
        }

        // Get first
        if (!services.isEmpty()) {
            return services.iterator().next();
        }

        throw new SAMLException("No assertion consumer services could be found for " + spssoDescriptor);
    }

    /**
     * <p>getProfileRequestContext.</p>
     *
     * @return a {@link ProfileRequestContext} object
     */
    public final ProfileRequestContext getProfileRequestContext() {
        return getMessageContext().getSubcontext(ProfileRequestContext.class, true);
    }

    /**
     * <p>getSAMLSelfEntityContext.</p>
     *
     * @return a {@link SAMLSelfEntityContext} object
     */
    public final SAMLSelfEntityContext getSAMLSelfEntityContext() {
        return getMessageContext().getSubcontext(SAMLSelfEntityContext.class, true);
    }

    /**
     * <p>getSOAP11Context.</p>
     *
     * @return a {@link SOAP11Context} object
     */
    public final SOAP11Context getSOAP11Context() {
        return getMessageContext().getSubcontext(SOAP11Context.class, true);
    }

    /**
     * <p>getSAMLSelfMetadataContext.</p>
     *
     * @return a {@link SAMLMetadataContext} object
     */
    public final SAMLMetadataContext getSAMLSelfMetadataContext() {
        return getSAMLSelfEntityContext().getSubcontext(SAMLMetadataContext.class, true);
    }

    /**
     * <p>getSAMLPeerMetadataContext.</p>
     *
     * @return a {@link SAMLMetadataContext} object
     */
    public final SAMLMetadataContext getSAMLPeerMetadataContext() {
        return getSAMLPeerEntityContext().getSubcontext(SAMLMetadataContext.class, true);
    }

    /**
     * <p>getSAMLPeerEntityContext.</p>
     *
     * @return a {@link SAMLPeerEntityContext} object
     */
    public final SAMLPeerEntityContext getSAMLPeerEntityContext() {
        return getMessageContext().getSubcontext(SAMLPeerEntityContext.class, true);
    }

    /**
     * <p>getSAMLSubjectNameIdentifierContext.</p>
     *
     * @return a {@link SAMLSubjectNameIdentifierContext} object
     */
    public final SAMLSubjectNameIdentifierContext getSAMLSubjectNameIdentifierContext() {
        return getMessageContext().getSubcontext(SAMLSubjectNameIdentifierContext.class, true);
    }

    /**
     * <p>getSAMLPeerEndpointContext.</p>
     *
     * @return a {@link SAMLEndpointContext} object
     */
    public final SAMLEndpointContext getSAMLPeerEndpointContext() {
        return getSAMLPeerEntityContext().getSubcontext(SAMLEndpointContext.class, true);
    }

    /**
     * <p>getSAMLSelfEndpointContext.</p>
     *
     * @return a {@link SAMLEndpointContext} object
     */
    public final SAMLEndpointContext getSAMLSelfEndpointContext() {
        return getSAMLSelfEntityContext().getSubcontext(SAMLEndpointContext.class, true);
    }

    /**
     * <p>getSAMLBindingContext.</p>
     *
     * @return a {@link SAMLBindingContext} object
     */
    public final SAMLBindingContext getSAMLBindingContext() {
        return getMessageContext().getSubcontext(SAMLBindingContext.class, true);
    }

    /**
     * <p>getSecurityParametersContext.</p>
     *
     * @return a {@link SecurityParametersContext} object
     */
    public final SecurityParametersContext getSecurityParametersContext() {
        return getMessageContext().getSubcontext(SecurityParametersContext.class, true);
    }

    /**
     * <p>getSAMLSelfProtocolContext.</p>
     *
     * @return a {@link SAMLProtocolContext} object
     */
    public final SAMLProtocolContext getSAMLSelfProtocolContext() {
        return this.getSAMLSelfEntityContext().getSubcontext(SAMLProtocolContext.class, true);
    }

    /**
     * <p>getSAMLProtocolContext.</p>
     *
     * @return a {@link SAMLProtocolContext} object
     */
    public final SAMLProtocolContext getSAMLProtocolContext() {
        return getMessageContext().getSubcontext(SAMLProtocolContext.class, true);
    }

    /**
     * <p>getProfileRequestContextOutboundMessageTransportResponse.</p>
     *
     * @return a {@link Pac4jSAMLResponse} object
     */
    public final Pac4jSAMLResponse getProfileRequestContextOutboundMessageTransportResponse() {
        return (Pac4jSAMLResponse) getProfileRequestContext().getOutboundMessageContext().getMessage();
    }

    /**
     * <p>getSAMLEndpointContext.</p>
     *
     * @return a {@link SAMLEndpointContext} object
     */
    public final SAMLEndpointContext getSAMLEndpointContext() {
        return getMessageContext().getSubcontext(SAMLEndpointContext.class, true);
    }
}
