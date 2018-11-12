package org.pac4j.saml.context;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLEndpointContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLProtocolContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.opensaml.soap.messaging.context.SOAP11Context;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.storage.SAMLMessageStorage;
import org.pac4j.saml.transport.Pac4jSAMLResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow to store additional information for SAML processing.
 * 
 * @author Michael Remond
 * @version 1.5.0
 */
@SuppressWarnings("rawtypes")
public class SAML2MessageContext extends MessageContext<SAMLObject> {

    private WebContext webContext;

    /* valid subject assertion */
    private Assertion subjectAssertion;

    /** BaseID retrieved either from the Subject or from a SubjectConfirmation */
    private BaseID baseID;

    /** SubjectConfirmations used during assertion evaluation. */
    private List<SubjectConfirmation> subjectConfirmations = new ArrayList<>();

    private SAMLMessageStorage samlMessageStorage;


    public SAML2MessageContext() {
        super();
    }

    public SAML2MessageContext(final MessageContext<SAMLObject> ctx) {
        this();
        super.setParent(ctx);
    }

    public WebContext getWebContext() {
        return webContext;
    }

    public void setWebContext(final WebContext webContext) {
        this.webContext = webContext;
    }

    public final Assertion getSubjectAssertion() {
        return this.subjectAssertion;
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

    public final SingleSignOnService getIDPSingleSignOnService(final String binding) {
        final List<SingleSignOnService> services = getIDPSSODescriptor().getSingleSignOnServices();
        for (final SingleSignOnService service : services) {
            if (service.getBinding().equals(binding)) {
                return service;
            }
        }
        throw new SAMLException("Identity provider has no single sign on service available for the selected profile"
                + binding);
    }

    public final AssertionConsumerService getSPAssertionConsumerService() {
        return getSPAssertionConsumerService(null);
    }

    public final AssertionConsumerService getSPAssertionConsumerService(final String acsIndex) {
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
        return this.getSubcontext(ProfileRequestContext.class, true);
    }

    public final SAMLSelfEntityContext getSAMLSelfEntityContext() {
        return this.getSubcontext(SAMLSelfEntityContext.class, true);
    }

    public final SOAP11Context getSOAP11Context() {
        return this.getSubcontext(SOAP11Context.class, true);
    }

    public final SAMLMetadataContext getSAMLSelfMetadataContext() {
        return getSAMLSelfEntityContext().getSubcontext(SAMLMetadataContext.class, true);
    }

    public final SAMLMetadataContext getSAMLPeerMetadataContext() {
        return getSAMLPeerEntityContext().getSubcontext(SAMLMetadataContext.class, true);
    }

    public final SAMLPeerEntityContext getSAMLPeerEntityContext() {
        return this.getSubcontext(SAMLPeerEntityContext.class, true);
    }

    public final SAMLSubjectNameIdentifierContext getSAMLSubjectNameIdentifierContext() {
        return this.getSubcontext(SAMLSubjectNameIdentifierContext.class, true);
    }

    public final void setSubjectAssertion(final Assertion subjectAssertion) {
        this.subjectAssertion = subjectAssertion;
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
        return this.getSubcontext(SAMLBindingContext.class, true);
    }

    public final SecurityParametersContext getSecurityParametersContext() {
        return this.getSubcontext(SecurityParametersContext.class, true);
    }

    public final SAMLProtocolContext getSAMLSelfProtocolContext() {
        return this.getSAMLSelfEntityContext().getSubcontext(SAMLProtocolContext.class, true);
    }

    public final SAMLProtocolContext getSAMLProtocolContext() {
        return this.getSubcontext(SAMLProtocolContext.class, true);
    }

    public final Pac4jSAMLResponse getProfileRequestContextOutboundMessageTransportResponse() {
        return (Pac4jSAMLResponse) getProfileRequestContext().getOutboundMessageContext().getMessage();
    }

    public final SAMLEndpointContext getSAMLEndpointContext() {
        return this.getSubcontext(SAMLEndpointContext.class, true);
    }

    public final SAMLMessageStorage getSAMLMessageStorage() {
        return this.samlMessageStorage;
    }

    public final void setSAMLMessageStorage(final SAMLMessageStorage samlMessageStorage) {
        this.samlMessageStorage = samlMessageStorage;
    }
}
