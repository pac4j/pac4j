package org.pac4j.saml.sso.impl;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;

import java.util.List;

/**
 * Validator for SAML logout request/response from the IdP.
 * 
 * @author Matthieu Taggiasco
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutResponseValidator extends AbstractSAML2ResponseValidator {

    public SAML2LogoutResponseValidator(final SAML2SignatureTrustEngineProvider engine, final Decrypter decrypter) {
        super(engine, decrypter);
    }

    /**
     * Validates the SAML protocol logout request/response.
     *
     * @param context the context
     */
    @Override
    public Credentials validate(final SAML2MessageContext context) {

        final SAMLObject message = context.getMessage();

        // IDP-initiated or CAS v5/v6?
        if (message instanceof LogoutRequest) {
            final LogoutRequest logoutRequest = (LogoutRequest) message;
            final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
            validateLogoutRequest(logoutRequest, context, engine);

        } else if (message instanceof LogoutResponse) {
            // SP-initiated
            final LogoutResponse logoutResponse = (LogoutResponse) message;
            final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
            validateLogoutResponse(logoutResponse, context, engine);

        } else {
            throw new SAMLException("Must be a LogoutRequest or LogoutResponse type");
        }

        return null;
    }

    /**
     * Validates the SAML logout request.
     *
     * @param logoutRequest the logout request
     * @param context the context
     * @param engine the signature engine
     */
    protected void validateLogoutRequest(final LogoutRequest logoutRequest, final SAML2MessageContext context,
                                               final SignatureTrustEngine engine) {

        validateSignatureIfItExists(logoutRequest.getSignature(), context, engine);

        // don't check because of CAS v5
        // validateIssueInstant(logoutRequest.getIssueInstant());

        validateIssuerIfItExists(logoutRequest.getIssuer(), context);

        final EncryptedID encryptedID = logoutRequest.getEncryptedID();
        if (encryptedID != null) {
            decryptEncryptedId(encryptedID, decrypter);
        }

        final List<SessionIndex> sessionIndexes = logoutRequest.getSessionIndexes();
        if (sessionIndexes == null || sessionIndexes.size() != 1) {
            throw new SAMLException("We must have one session index in the logout request");
        }
        //final String sessionIndex = sessionIndexes.get(0).getSessionIndex();
    }

    /**
     * Validates the SAML logout response.
     *
     * @param logoutResponse the logout response
     * @param context the context
     * @param engine the signature engine
     */
    protected void validateLogoutResponse(final LogoutResponse logoutResponse, final SAML2MessageContext context,
                                               final SignatureTrustEngine engine) {

        validateSuccess(logoutResponse.getStatus());

        validateSignatureIfItExists(logoutResponse.getSignature(), context, engine);

        validateIssueInstant(logoutResponse.getIssueInstant());

        validateIssuerIfItExists(logoutResponse.getIssuer(), context);

        verifyEndpoint(context.getSPSSODescriptor().getSingleLogoutServices().get(0), logoutResponse.getDestination());

        // sessionindex ?
    }

    @Override
    public final void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
    }
}
