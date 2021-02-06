package org.pac4j.saml.logout.impl;

import net.shibboleth.utilities.java.support.net.URIComparator;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.SessionIndex;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.OkAction;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.impl.AbstractSAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.util.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Validator for SAML logout requests/responses from the IdP.
 *
 * @author Matthieu Taggiasco
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class SAML2LogoutValidator extends AbstractSAML2ResponseValidator {

    private String postLogoutURL;

    /**
     * When set to false, will cause the validator
     * to not throw back exceptions and expect adaptations of those exceptions
     * when the response is successfully validated. Instead, the validator should successfully
     * move on without throwing {@link OkAction}.
     */
    private boolean actionOnSuccess = true;

    /**
     * Expected destination endpoint when validating saml2 logout responses.
     * If left blank, will use the SLO endpoint from metadata context.
     */
    private String expectedDestination;

    public SAML2LogoutValidator(final SAML2SignatureTrustEngineProvider engine, final Decrypter decrypter,
                                final LogoutHandler logoutHandler, final String postLogoutURL,
                                final ReplayCacheProvider replayCache, final URIComparator uriComparator) {
        super(engine, decrypter, logoutHandler, replayCache, uriComparator);
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

            final HttpAction action = handlePostLogoutResponse(context);
            if (action != null) {
                throw action;
            }
            return null;
        }
        throw new SAMLException("SAML message must be a LogoutRequest or LogoutResponse type");
    }

    protected HttpAction handlePostLogoutResponse(final SAML2MessageContext context) {
        if (StringUtils.isNotBlank(postLogoutURL)) {
            // if custom post logout URL is present then redirect to it
            return new FoundAction(postLogoutURL);
        }
        // nothing to reply to the logout response
        return this.actionOnSuccess ? new OkAction("") : null;
    }

    /**
     * Validates the SAML logout request.
     *
     * @param logoutRequest the logout request
     * @param context       the context
     * @param engine        the signature engine
     */
    protected void validateLogoutRequest(final LogoutRequest logoutRequest, final SAML2MessageContext context,
                                         final SignatureTrustEngine engine) {

        if (logger.isTraceEnabled()) {
            logger.trace("Validating logout request:\n{}", Configuration.serializeSamlObject(logoutRequest));
        }
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
            logger.debug("Using SLO key {} as the session index with the binding uri {}", sloKey, bindingUri);
            if (SAMLConstants.SAML2_SOAP11_BINDING_URI.equals(bindingUri)) {
                logoutHandler.destroySessionBack(context.getWebContext(), context.getSessionStore(), sloKey);
            } else {
                logoutHandler.destroySessionFront(context.getWebContext(), context.getSessionStore(), sloKey);
            }
        }
    }

    /**
     * Validates the SAML logout response.
     *
     * @param logoutResponse the logout response
     * @param context        the context
     * @param engine         the signature engine
     */
    protected void validateLogoutResponse(final LogoutResponse logoutResponse, final SAML2MessageContext context,
                                          final SignatureTrustEngine engine) {

        if (logger.isTraceEnabled()) {
            logger.trace("Validating logout response:\n{}", Configuration.serializeSamlObject(logoutResponse));
        }
        validateSuccess(logoutResponse.getStatus());

        validateSignatureIfItExists(logoutResponse.getSignature(), context, engine);

        validateIssueInstant(logoutResponse.getIssueInstant());

        validateIssuerIfItExists(logoutResponse.getIssuer(), context);

        validateDestinationEndpoint(logoutResponse, context);
    }

    protected void validateDestinationEndpoint(final LogoutResponse logoutResponse, final SAML2MessageContext context) {
        final List<String> expected = new ArrayList<>();
        if (StringUtils.isBlank(this.expectedDestination)) {
            final Endpoint endpoint = Objects.requireNonNull(context.getSPSSODescriptor().getSingleLogoutServices().get(0));
            if (endpoint.getLocation() != null) {
                expected.add(endpoint.getLocation());
            }
            if (endpoint.getResponseLocation() != null) {
                expected.add(endpoint.getResponseLocation());
            }
        } else {
            expected.add(this.expectedDestination);
        }
        verifyEndpoint(expected, logoutResponse.getDestination());
    }

    public void setActionOnSuccess(final boolean actionOnSuccess) {
        this.actionOnSuccess = actionOnSuccess;
    }

    public void setPostLogoutURL(final String postLogoutURL) {
        this.postLogoutURL = postLogoutURL;
    }

    public void setExpectedDestination(final String expectedDestination) {
        this.expectedDestination = expectedDestination;
    }

    public String getPostLogoutURL() {
        return postLogoutURL;
    }

    public boolean isActionOnSuccess() {
        return actionOnSuccess;
    }

    public String getExpectedDestination() {
        return expectedDestination;
    }

    @Override
    public final void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
    }
}
