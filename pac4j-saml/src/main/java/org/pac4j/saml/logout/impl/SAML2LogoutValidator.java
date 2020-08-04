package org.pac4j.saml.logout.impl;

import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.impl.AbstractSAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;

import java.util.List;

/**
 * Validator for SAML logout requests/responses from the IdP.
 * 
 * @author Matthieu Taggiasco
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutValidator extends AbstractSAML2ResponseValidator {

    private String postLogoutURL;

    public SAML2LogoutValidator(final SAML2SignatureTrustEngineProvider engine, final Decrypter decrypter,
                                final LogoutHandler logoutHandler, final String postLogoutURL, final ReplayCacheProvider replayCache) {
        super(engine, decrypter, logoutHandler, replayCache);
        this.postLogoutURL = postLogoutURL;
    }

    /**
     * Validates the SAML protocol logout request/response.
     *
     * @param context the context
     */
    @Override
    public Credentials validate(final SAML2MessageContext context) {
        final SAMLObject message = (SAMLObject) context.getMessageContext().getMessage();

        // IDP-initiated
        if (message instanceof LogoutRequest) {
            final LogoutRequest logoutRequest = (LogoutRequest) message;
            final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
            validateLogoutRequest(logoutRequest, context, engine);

            return null;

        } else if (message instanceof LogoutResponse) {
            // SP-initiated
            final LogoutResponse logoutResponse = (LogoutResponse) message;
            final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
            validateLogoutResponse(logoutResponse, context, engine);

            if (StringUtils.isNotBlank(postLogoutURL)){
                // if custom post logout URL is present then redirect to it
                throw new FoundAction(postLogoutURL);
            } else {
                // nothing to reply to the logout response
                throw new OkAction("");
            }
        } else {
            throw new SAMLException("Must be a LogoutRequest or LogoutResponse type");
        }
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
        //validateIssueInstant(logoutRequest.getIssueInstant());

        validateIssuerIfItExists(logoutRequest.getIssuer(), context);

        NameID nameId = logoutRequest.getNameID();
        final EncryptedID encryptedID = logoutRequest.getEncryptedID();
        if (encryptedID != null) {
            nameId = decryptEncryptedId(encryptedID, decrypter);
        }

        String sessionIndex = null;
        final List<SessionIndex> sessionIndexes = logoutRequest.getSessionIndexes();
        if (sessionIndexes != null && !sessionIndexes.isEmpty()) {
            final SessionIndex sessionIndexObject = sessionIndexes.get(0);
            if (sessionIndexObject != null) {
                sessionIndex = sessionIndexObject.getValue();
            }
        }

        final String sloKey = computeSloKey(sessionIndex, nameId);
        if (sloKey != null) {
            final String bindingUri = context.getSAMLBindingContext().getBindingUri();
            if (SAMLConstants.SAML2_SOAP11_BINDING_URI.equals(bindingUri)) {
                logoutHandler.destroySessionBack(context.getWebContext(), sloKey);
            } else {
                logoutHandler.destroySessionFront(context.getWebContext(), sloKey);
            }
        }
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
    }

    @Override
    public final void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
    }
}
