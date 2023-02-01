package org.pac4j.saml.logout.impl;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import net.shibboleth.shared.net.URIComparator;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.LogoutResponse;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.logout.LogoutType;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2AuthenticationCredentials;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.profile.impl.AbstractSAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.util.Configuration;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Validator for SAML logout requests/responses from the IdP.
 *
 * @author Matthieu Taggiasco
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
@Setter
public class SAML2LogoutValidator extends AbstractSAML2ResponseValidator {

    /**
     * Logouts are only successful if the IdP was able to inform all services, otherwise it will
     * respond with PartialLogout. This setting allows clients to ignore such server-side problems.
     */
    private boolean isPartialLogoutTreatedAsSuccess = false;

    /**
     * Expected destination endpoint when validating saml2 logout responses.
     * If left blank, will use the SLO endpoint from metadata context.
     */
    private String expectedDestination;

    public SAML2LogoutValidator(final SAML2SignatureTrustEngineProvider engine, final Decrypter decrypter,
                                final SessionLogoutHandler logoutHandler, final ReplayCacheProvider replayCache,
                                final URIComparator uriComparator) {
        super(engine, decrypter, logoutHandler, replayCache, uriComparator);
    }

    /**
     * Validates the SAML protocol logout request/response.
     *
     * @param context the context
     */
    @Override
    public Credentials validate(final SAML2MessageContext context) {
        val message = (SAMLObject) context.getMessageContext().getMessage();

        // IDP-initiated
        if (message instanceof LogoutRequest logoutRequest) {
            val engine = this.signatureTrustEngineProvider.build();
            validateLogoutRequest(logoutRequest, context, engine);
            return new SAML2Credentials(LogoutType.UNDEFINED, context);

        // SP-initiated
        } else if (message instanceof LogoutResponse logoutResponse) {
            val engine = this.signatureTrustEngineProvider.build();
            validateLogoutResponse(logoutResponse, context, engine);
            return new SAML2Credentials(LogoutType.UNDEFINED, context);
        }

        throw new SAMLException("SAML message must be a LogoutRequest or LogoutResponse type");
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

        logger.trace("Validating logout request:\n{}", Configuration.serializeSamlObject(logoutRequest));
        validateSignatureIfItExists(logoutRequest.getSignature(), context, engine);

        // don't check because of CAS v5
        //validateIssueInstant(logoutRequest.getIssueInstant());

        validateIssuerIfItExists(logoutRequest.getIssuer(), context);

        var nameId = logoutRequest.getNameID();
        val encryptedID = logoutRequest.getEncryptedID();
        if (encryptedID != null) {
            nameId = decryptEncryptedId(encryptedID, decrypter);
        }

        val samlNameId = SAML2AuthenticationCredentials.SAMLNameID.from(nameId);
        String sessionIndex = null;
        val sessionIndexes = logoutRequest.getSessionIndexes();
        if (sessionIndexes != null && !sessionIndexes.isEmpty()) {
            val sessionIndexObject = sessionIndexes.get(0);
            if (sessionIndexObject != null) {
                sessionIndex = sessionIndexObject.getValue();
            }
        }

        val sloKey = computeSloKey(sessionIndex, samlNameId);
        if (sloKey != null) {
            logoutHandler.destroySession(context.getCallContext(), sloKey);
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

        logger.trace("Validating logout response:\n{}", Configuration.serializeSamlObject(logoutResponse));
        validateSuccess(logoutResponse.getStatus());

        validateSignatureIfItExists(logoutResponse.getSignature(), context, engine);

        validateIssueInstant(logoutResponse.getIssueInstant());

        validateIssuerIfItExists(logoutResponse.getIssuer(), context);

        validateDestinationEndpoint(logoutResponse, context);
    }

    protected void validateDestinationEndpoint(final LogoutResponse logoutResponse, final SAML2MessageContext context) {
        val expected = new ArrayList<String>();
        if (CommonHelper.isBlank(this.expectedDestination)) {
            val endpoint = Objects.requireNonNull(context.getSPSSODescriptor().getSingleLogoutServices().get(0));
            if (endpoint.getLocation() != null) {
                expected.add(endpoint.getLocation());
            }
            if (endpoint.getResponseLocation() != null) {
                expected.add(endpoint.getResponseLocation());
            }
        } else {
            expected.add(this.expectedDestination);
        }

        val isDestinationMandatory = context.getSaml2Configuration().isResponseDestinationAttributeMandatory();
        verifyEndpoint(expected, logoutResponse.getDestination(), isDestinationMandatory);
    }

    @Override
    protected void validateSuccess(Status status) {

        if (isPartialLogoutTreatedAsSuccess && status != null && status.getStatusCode() != null) {

            if (StatusCode.PARTIAL_LOGOUT.equals(status.getStatusCode().getValue())) {
                logger.debug(
                    "Response status code is {} and partial logouts are configured to be treated as success => validation successful!",
                    StatusCode.PARTIAL_LOGOUT);
                return;
            }

            logger.debug("Response status code: {}", status.getStatusCode().getValue());

            if (StatusCode.RESPONDER.equals(status.getStatusCode().getValue())
                && status.getStatusCode().getOrderedChildren().stream().filter(StatusCode.class::isInstance).map(StatusCode.class::cast)
                .anyMatch(code -> StatusCode.PARTIAL_LOGOUT.equals(code.getValue()))) {
                logger.debug(
                    "Response sub-status code is {} and partial logouts are configured to be treated as success => validation successful!",
                    StatusCode.PARTIAL_LOGOUT);
                return;
            }
        }

        super.validateSuccess(status);
    }
}
