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

    public SAML2MessageContext(final CallContext callContext) {
        this.callContext = callContext;
    }

    public SAML2ConfigurationContext getConfigurationContext() {
        val webContext = callContext.webContext();
        CommonHelper.assertNotNull("webContext", webContext);
        CommonHelper.assertNotNull("saml2Configuration", this.saml2Configuration);
        return new SAML2ConfigurationContext(webContext, this.saml2Configuration);
    }

    public final SPSSODescriptor getSPSSODescriptor() {
        val selfContext = getSAMLSelfMetadataContext();
        val spDescriptor = (SPSSODescriptor) selfContext.getRoleDescriptor();
        return spDescriptor;
    }

    public final IDPSSODescriptor getIDPSSODescriptor() {
        val peerContext = getSAMLPeerMetadataContext();
        val idpssoDescriptor = (IDPSSODescriptor) peerContext.getRoleDescriptor();
        return idpssoDescriptor;
    }

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

    public AssertionConsumerService getSPAssertionConsumerService() {
        val spssoDescriptor = getSPSSODescriptor();
        return getSPAssertionConsumerService(spssoDescriptor, spssoDescriptor.getAssertionConsumerServices());
    }

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

    protected AssertionConsumerService getSPAssertionConsumerService(
        final SPSSODescriptor spssoDescriptor,
        final List<AssertionConsumerService> services) {

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

    public final ProfileRequestContext getProfileRequestContext() {
        return getMessageContext().getSubcontext(ProfileRequestContext.class, true);
    }

    public final SAMLSelfEntityContext getSAMLSelfEntityContext() {
        return getMessageContext().getSubcontext(SAMLSelfEntityContext.class, true);
    }

    public final SOAP11Context getSOAP11Context() {
        return getMessageContext().getSubcontext(SOAP11Context.class, true);
    }

    public final SAMLMetadataContext getSAMLSelfMetadataContext() {
        return getSAMLSelfEntityContext().getSubcontext(SAMLMetadataContext.class, true);
    }

    public final SAMLMetadataContext getSAMLPeerMetadataContext() {
        return getSAMLPeerEntityContext().getSubcontext(SAMLMetadataContext.class, true);
    }

    public final SAMLPeerEntityContext getSAMLPeerEntityContext() {
        return getMessageContext().getSubcontext(SAMLPeerEntityContext.class, true);
    }

    public final SAMLSubjectNameIdentifierContext getSAMLSubjectNameIdentifierContext() {
        return getMessageContext().getSubcontext(SAMLSubjectNameIdentifierContext.class, true);
    }

    public final SAMLEndpointContext getSAMLPeerEndpointContext() {
        return getSAMLPeerEntityContext().getSubcontext(SAMLEndpointContext.class, true);
    }

    public final SAMLEndpointContext getSAMLSelfEndpointContext() {
        return getSAMLSelfEntityContext().getSubcontext(SAMLEndpointContext.class, true);
    }

    public final SAMLBindingContext getSAMLBindingContext() {
        return getMessageContext().getSubcontext(SAMLBindingContext.class, true);
    }

    public final SecurityParametersContext getSecurityParametersContext() {
        return getMessageContext().getSubcontext(SecurityParametersContext.class, true);
    }

    public final SAMLProtocolContext getSAMLSelfProtocolContext() {
        return this.getSAMLSelfEntityContext().getSubcontext(SAMLProtocolContext.class, true);
    }

    public final SAMLProtocolContext getSAMLProtocolContext() {
        return getMessageContext().getSubcontext(SAMLProtocolContext.class, true);
    }

    public final Pac4jSAMLResponse getProfileRequestContextOutboundMessageTransportResponse() {
        return (Pac4jSAMLResponse) getProfileRequestContext().getOutboundMessageContext().getMessage();
    }

    public final SAMLEndpointContext getSAMLEndpointContext() {
        return getMessageContext().getSubcontext(SAMLEndpointContext.class, true);
    }
}
