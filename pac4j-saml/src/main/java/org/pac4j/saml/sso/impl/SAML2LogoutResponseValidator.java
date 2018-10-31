package org.pac4j.saml.sso.impl;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;

import java.util.List;

/**
 * Validator for SAML logout request from the IdP.
 * 
 * @author Matthieu Taggiasco
 * @since 2.0.0
 */
public class SAML2LogoutResponseValidator extends AbstractSAML2ResponseValidator {

    public SAML2LogoutResponseValidator(final SAML2SignatureTrustEngineProvider engine) {
        super(engine);
    }

    /**
     * Validates the SAML protocol logout request.
     * The method decrypt encrypted assertions if any.
     *
     * @param context the context
     */
    @Override
    public Credentials validate(final SAML2MessageContext context) {

        final SAMLObject message = context.getMessage();

        if (!(message instanceof LogoutRequest)) {
            throw new SAMLException("Must be a LogoutRequest type");
        }

        final LogoutRequest logoutRequest = (LogoutRequest) message;
        final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
        validateSamlProtocolResponse(logoutRequest, context, engine);

        return null;
    }

    /**
     * Validates the SAML logout request:
     *  - IssueInstant
     *  - Issuer
     *  - Signature
     *
     * @param logoutRequest the logout request
     * @param context the context
     */
    protected final void validateSamlProtocolResponse(final LogoutRequest logoutRequest, final SAML2MessageContext context,
                                                      final SignatureTrustEngine engine) {

        if (logoutRequest.getSignature() != null) {
            final String entityId = context.getSAMLPeerEntityContext().getEntityId();
            validateSignature(logoutRequest.getSignature(), entityId, engine);
            context.getSAMLPeerEntityContext().setAuthenticated(true);
        }

        // don't check because of CAS v5
        /*if (!isIssueInstantValid(logoutRequest.getIssueInstant())) {
            throw new SAMLIssueInstantException("LogoutRequest issue instant is too old or in the future");
        }*/
        
        if (logoutRequest.getIssuer() != null) {
            validateIssuer(logoutRequest.getIssuer(), context);
        }

        final List<SessionIndex> sessionIndexes = logoutRequest.getSessionIndexes();
        if (sessionIndexes == null || sessionIndexes.size() != 1) {
            throw new SAMLException("We must have one session index in the logout request");
        }
        //final String sessionIndex = sessionIndexes.get(0).getSessionIndex();
    }

    @Override
    public final void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
    }
}
