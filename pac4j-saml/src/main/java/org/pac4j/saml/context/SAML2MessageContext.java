package org.pac4j.saml.context;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.StatusResponseType;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
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
public class SAML2MessageContext {

    /**
     * SubjectConfirmations used during assertion evaluation.
     */
    private final List<SubjectConfirmation> subjectConfirmations = new ArrayList<>();

    private MessageContext messageContext = new MessageContext();

    private WebContext webContext;

    private SessionStore sessionStore;

    /* valid subject assertion */
    private Assertion subjectAssertion;

    /**
     * BaseID retrieved either from the Subject or from a SubjectConfirmation
     */
    private BaseID baseID;

    private SAMLMessageStore samlMessageStore;

    public SAML2MessageContext() {
        super();
    }

    public MessageContext getMessageContext() {
        return messageContext;
    }

    public void setMessageContext(final MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    public WebContext getWebContext() {
        return webContext;
    }

    public void setWebContext(final WebContext webContext) {
        this.webContext = webContext;
    }

    public SessionStore getSessionStore() {
        return sessionStore;
    }

    public void setSessionStore(final SessionStore sessionStore) {
        this.sessionStore = sessionStore;
    }

    public final Assertion getSubjectAssertion() {
        return this.subjectAssertion;
    }

    public final void setSubjectAssertion(final Assertion subjectAssertion) {
        this.subjectAssertion = subjectAssertion;
    }

    public final SPSSODescriptor getSPSSODescriptor() {
        final SAMLMetadataContext selfContext = getSAMLSelfMetadataContext();
        final SPSSODescriptor spDescriptor = (SPSSODescriptor) selfContext.getRoleDescriptor();
        return spDescriptor;
    }

    public final IDPSSODescriptor getIDPSSODescriptor() {
        final SAMLMetadataContext peerContext = getSAMLPeerMetadataContext();
        final IDPSSODescriptor idpssoDescriptor = (IDPSSODescriptor) peerContext.getRoleDescriptor();
        return idpssoDescriptor;
    }

    public final SingleLogoutService getIDPSingleLogoutService(final String binding) {
        final List<SingleLogoutService> services = getIDPSSODescriptor().getSingleLogoutServices();
        for (final SingleLogoutService service : services) {
            if (service.getBinding().equals(binding)) {
                return service;
            }
        }
        throw new SAMLException("Identity provider has no single logout service available for the selected profile"
            + binding);
    }

    public SingleSignOnService getIDPSingleSignOnService(final String binding) {
        final List<SingleSignOnService> services = getIDPSSODescriptor().getSingleSignOnServices();
        for (final SingleSignOnService service : services) {
            if (service.getBinding().equals(binding)) {
                return service;
            }
        }
        throw new SAMLException("Identity provider has no single sign on service available for the selected profile"
            + binding);
    }

    public AssertionConsumerService getSPAssertionConsumerService() {
        final SPSSODescriptor spssoDescriptor = getSPSSODescriptor();
        return getSPAssertionConsumerService(spssoDescriptor, spssoDescriptor.getAssertionConsumerServices());
    }

    public AssertionConsumerService getSPAssertionConsumerService(final StatusResponseType response) {
        final SPSSODescriptor spssoDescriptor = getSPSSODescriptor();
        final List<AssertionConsumerService> services = spssoDescriptor.getAssertionConsumerServices();

        // Get by index
        if (response != null && StringUtils.isNotEmpty(response.getDestination())) {
            for (final AssertionConsumerService service : services) {
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
        final SPSSODescriptor spssoDescriptor = getSPSSODescriptor();
        final List<AssertionConsumerService> services = spssoDescriptor.getAssertionConsumerServices();

        // Get by index
        if (acsIndex != null) {
            for (final AssertionConsumerService service : services) {
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

    public final BaseID getBaseID() {
        return baseID;
    }

    public final void setBaseID(final BaseID baseID) {
        this.baseID = baseID;
    }

    public final List<SubjectConfirmation> getSubjectConfirmations() {
        return subjectConfirmations;
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

    public final SAMLMessageStore getSAMLMessageStore() {
        return this.samlMessageStore;
    }

    public final void setSAMLMessageStore(final SAMLMessageStore samlMessageStore) {
        this.samlMessageStore = samlMessageStore;
    }
}
