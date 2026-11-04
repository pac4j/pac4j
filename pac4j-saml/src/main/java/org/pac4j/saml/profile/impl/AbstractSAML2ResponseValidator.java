package org.pac4j.saml.profile.impl;


import lombok.val;
import net.shibboleth.shared.codec.Base64Support;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.net.URIComparator;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.security.impl.MessageReplaySecurityHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.core.*;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.logout.handler.SessionLogoutHandler;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2AuthenticationCredentials;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.*;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The abstract class for all SAML response validators.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractSAML2ResponseValidator implements SAML2ResponseValidator {

    private static final String SAML_REQUEST_PARAMETER = "SAMLRequest";

    private static final String SAML_RESPONSE_PARAMETER = "SAMLResponse";

    private static final String RELAY_STATE_PARAMETER = "RelayState";

    private static final String SIG_ALG_PARAMETER = "SigAlg";

    private static final String SIGNATURE_PARAMETER = "Signature";

    private static final Set<String> REDIRECT_SIGNED_PARAMETERS = Set.of(SAML_REQUEST_PARAMETER, SAML_RESPONSE_PARAMETER,
        RELAY_STATE_PARAMETER, SIG_ALG_PARAMETER, SIGNATURE_PARAMETER);

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected final URIComparator uriComparator;

    protected final Decrypter decrypter;

    protected final SessionLogoutHandler logoutHandler;

    protected final ReplayCacheProvider replayCache;

    /* maximum skew in seconds between SP and IDP clocks */
    protected long clockSkewSeconds = 120;

    /**
     * <p>Constructor for AbstractSAML2ResponseValidator.</p>
     *
     * @param signatureTrustEngineProvider a {@link SAML2SignatureTrustEngineProvider} object
     * @param decrypter a {@link Decrypter} object
     * @param logoutHandler a {@link SessionLogoutHandler} object
     * @param replayCache a {@link ReplayCacheProvider} object
     * @param uriComparator a {@link URIComparator} object
     */
    protected AbstractSAML2ResponseValidator(final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider,
                                             final Decrypter decrypter, final SessionLogoutHandler logoutHandler,
                                             final ReplayCacheProvider replayCache, final URIComparator uriComparator) {
        this.signatureTrustEngineProvider = signatureTrustEngineProvider;
        this.decrypter = decrypter;
        this.logoutHandler = logoutHandler;
        this.replayCache = replayCache;
        this.uriComparator = uriComparator;
    }

    /**
     * Validates that the response is a success.
     *
     * @param status the response status.
     */
    protected void validateSuccess(final Status status) {
        if (status == null || status.getStatusCode() == null) {
            throw new SAMLException("Missing response status or status code");
        }

        var statusValue = status.getStatusCode().getValue();
        if (!StatusCode.SUCCESS.equals(statusValue)) {
            val statusMessage = status.getStatusMessage();
            if (statusMessage != null) {
                statusValue += " / " + statusMessage.getValue();
            }
            throw new SAMLException("Response is not success ; actual " + statusValue);
        }
    }

    /**
     * <p>validateSignatureIfItExists.</p>
     *
     * @param signature a {@link Signature} object
     * @param context a {@link SAML2MessageContext} object
     * @param engine a {@link SignatureTrustEngine} object
     */
    protected void validateSignatureIfItExists(final Signature signature, final SAML2MessageContext context,
                                               final SignatureTrustEngine engine) {
        if (signature != null) {
            val entityId = context.getSAMLPeerEntityContext().getEntityId();
            validateSignature(signature, entityId, engine);
            context.getSAMLPeerEntityContext().setAuthenticated(true);
            logger.debug("Successfully validated signature for entity id {}", entityId);
        } else {
            logger.debug("Cannot locate a signature from the message; skipping validation");
        }
    }

    /**
     * Validate the given digital signature by checking its profile and value.
     *
     * @param signature   the signature
     * @param idpEntityId the idp entity id
     * @param trustEngine the trust engine
     */
    protected void validateSignature(final Signature signature, final String idpEntityId,
                                     final TrustEngine<Signature> trustEngine) {


        SignaturePrevalidator validator = new SAMLSignatureProfileValidator();
        try {
            logger.debug("Validating profile signature for entity id {}", idpEntityId);
            validator.validate(signature);
        } catch (final SignatureException e) {
            throw new SAMLSignatureValidationException("SAMLSignatureProfileValidator failed to validate signature", e);
        }

        final boolean valid;
        try {
            logger.debug("Validating signature via trust engine for entity id {}", idpEntityId);
            valid = trustEngine.validate(signature, buildSignatureCriteria(idpEntityId));
        } catch (final SecurityException e) {
            throw new SAMLSignatureValidationException("An error occurred during signature validation", e);
        }
        if (!valid) {
            throw new SAMLSignatureValidationException("Signature is not trusted");
        }
    }

    private static CriteriaSet buildSignatureCriteria(final String idpEntityId) {
        val criteriaSet = new CriteriaSet();
        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        criteriaSet.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        criteriaSet.add(new ProtocolCriterion(SAMLConstants.SAML20P_NS));
        criteriaSet.add(new EntityIdCriterion(idpEntityId));
        return criteriaSet;
    }

    /**
     * Validates the signature the HTTP-Redirect binding carries in the query string rather than in the XML message,
     * and marks the IdP as authenticated when it is valid.
     *
     * <p>The signed content is made of the {@code SAMLRequest} (or {@code SAMLResponse}), {@code RelayState} (if present)
     * and {@code SigAlg} parameters, in this order and with their values as URL-encoded in the query string
     * (SAML Bindings 2.0, §3.4.4.1).</p>
     *
     * @param context the context
     * @param engine  the signature engine
     */
    protected void validateRedirectBindingSignatureIfItExists(final SAML2MessageContext context, final SignatureTrustEngine engine) {
        if (!SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(context.getSAMLBindingContext().getBindingUri())) {
            return;
        }
        val queryString = context.getCallContext().webContext().getQueryString().orElse(null);
        if (StringUtils.isBlank(queryString)) {
            return;
        }

        // the raw values: the signature is computed on the URL-encoded form
        final Map<String, String> rawParameters = new HashMap<>();
        for (val pair : queryString.split("&")) {
            val name = StringUtils.substringBefore(pair, "=");
            if (REDIRECT_SIGNED_PARAMETERS.contains(name) && rawParameters.put(name, StringUtils.substringAfter(pair, "=")) != null) {
                // the decoded message must be the signed one
                throw new SAMLSignatureValidationException("Duplicate " + name + " parameter in the HTTP-Redirect query string");
            }
        }
        val rawSignature = rawParameters.get(SIGNATURE_PARAMETER);
        if (rawSignature == null) {
            logger.debug("Cannot locate a signature in the HTTP-Redirect query string; skipping validation");
            return;
        }
        val rawSigAlg = rawParameters.get(SIG_ALG_PARAMETER);
        val messageParameter = rawParameters.containsKey(SAML_REQUEST_PARAMETER) ? SAML_REQUEST_PARAMETER : SAML_RESPONSE_PARAMETER;
        val rawMessage = rawParameters.get(messageParameter);
        if (rawSigAlg == null || rawMessage == null
            || rawParameters.containsKey(SAML_REQUEST_PARAMETER) && rawParameters.containsKey(SAML_RESPONSE_PARAMETER)) {
            throw new SAMLSignatureValidationException("Incomplete HTTP-Redirect signature: one SAMLRequest or SAMLResponse "
                + "parameter and a SigAlg parameter are required");
        }

        val signedContent = new StringBuilder(messageParameter).append('=').append(rawMessage);
        if (rawParameters.containsKey(RELAY_STATE_PARAMETER)) {
            signedContent.append('&').append(RELAY_STATE_PARAMETER).append('=').append(rawParameters.get(RELAY_STATE_PARAMETER));
        }
        signedContent.append('&').append(SIG_ALG_PARAMETER).append('=').append(rawSigAlg);

        final String sigAlg;
        final byte[] signature;
        try {
            sigAlg = URLDecoder.decode(rawSigAlg, StandardCharsets.UTF_8);
            signature = Base64Support.decode(URLDecoder.decode(rawSignature, StandardCharsets.UTF_8));
        } catch (final Exception e) {
            throw new SAMLSignatureValidationException("Cannot decode the HTTP-Redirect signature", e);
        }
        val blackListedAlgorithms = context.getSaml2Configuration() != null
            ? context.getSaml2Configuration().getBlackListedSignatureSigningAlgorithms() : null;
        if (blackListedAlgorithms != null && blackListedAlgorithms.contains(sigAlg)) {
            throw new SAMLSignatureValidationException("Blacklisted signature algorithm: " + sigAlg);
        }

        val entityId = context.getSAMLPeerEntityContext().getEntityId();
        final boolean valid;
        try {
            logger.debug("Validating HTTP-Redirect signature via trust engine for entity id {}", entityId);
            valid = engine.validate(signature, signedContent.toString().getBytes(StandardCharsets.UTF_8), sigAlg,
                buildSignatureCriteria(entityId), null);
        } catch (final SecurityException e) {
            throw new SAMLSignatureValidationException("An error occurred during HTTP-Redirect signature validation", e);
        }
        if (!valid) {
            throw new SAMLSignatureValidationException("HTTP-Redirect signature is not trusted");
        }
        context.getSAMLPeerEntityContext().setAuthenticated(true);
        logger.debug("Successfully validated HTTP-Redirect signature for entity id {}", entityId);
    }

    /**
     * <p>validateIssuerIfItExists.</p>
     *
     * @param isser a {@link Issuer} object
     * @param context a {@link SAML2MessageContext} object
     */
    protected void validateIssuerIfItExists(final Issuer isser, final SAML2MessageContext context) {
        if (isser != null) {
            validateIssuer(isser, context);
        }
    }

    /**
     * Validate issuer format and value.
     *
     * @param issuer  the issuer
     * @param context the context
     */
    protected void validateIssuer(final NameIDType issuer, final SAML2MessageContext context) {
        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY)) {
            throw new SAMLIssuerException("Issuer type is not entity but " + issuer.getFormat());
        }

        val entityId = context.getSAMLPeerEntityContext().getEntityId();
        logger.debug("Comparing issuer {} against {}", issuer.getValue(), entityId);
        if (entityId == null || !entityId.equals(issuer.getValue())) {
            throw new SAMLIssuerException("Issuer " + issuer.getValue() + " does not match idp entityId " + entityId);
        }
    }

    /**
     * <p>validateIssueInstant.</p>
     *
     * @param issueInstant a {@link Instant} object
     */
    protected void validateIssueInstant(final Instant issueInstant) {
        if (!isIssueInstantValid(issueInstant)) {
            throw new SAMLIssueInstantException("Issue instant is too old or in the future");
        }
    }

    /**
     * <p>isIssueInstantValid.</p>
     *
     * @param issueInstant a {@link Instant} object
     * @return a boolean
     */
    protected boolean isIssueInstantValid(final Instant issueInstant) {
        return isDateValid(issueInstant, 0);
    }

    /**
     * <p>isDateValid.</p>
     *
     * @param issueInstant a {@link Instant} object
     * @param interval a long
     * @return a boolean
     */
    protected boolean isDateValid(final Instant issueInstant, final long interval) {
        val now = Instant.now();
        val before = now.plusSeconds(clockSkewSeconds);
        val after = now.minusSeconds(clockSkewSeconds + interval);

        val isDateValid = issueInstant.isBefore(before) && issueInstant.isAfter(after);
        if (!isDateValid) {
            logger.warn("interval={},before={},after={},issueInstant={}", interval, before, after, issueInstant);
        }
        return isDateValid;
    }

    /**
     * <p>verifyEndpoint.</p>
     *
     * @param endpoints a {@link java.util.List} object
     * @param destination a {@link String} object
     * @param isDestinationMandatory a boolean
     */
    protected void verifyEndpoint(final Collection<String> endpoints, final String destination, final boolean isDestinationMandatory) {
        if (destination == null && !isDestinationMandatory) {
            return;
        }
        if (destination == null) {
            throw new SAMLEndpointMismatchException("SAML configuration does not allow response Destination to be null");
        }

        val verified = endpoints.stream()
            .allMatch(endpoint -> compareEndpoints(destination, endpoint));
        if (!verified) {
            throw new SAMLEndpointMismatchException("Intended destination " + destination
                + " doesn't match any of the endpoint URLs  "
                + endpoints);
        }
    }

    /**
     * <p>compareEndpoints.</p>
     *
     * @param destination a {@link String} object
     * @param endpoint a {@link String} object
     * @return a boolean
     */
    protected boolean compareEndpoints(final String destination, final String endpoint) {
        try {
            return uriComparator.compare(destination, endpoint);
        } catch (final Exception e) {
            throw new SAMLEndpointMismatchException(e);
        }
    }

    /**
     * <p>verifyMessageReplay.</p>
     *
     * @param context a {@link SAML2MessageContext} object
     */
    protected void verifyMessageReplay(final SAML2MessageContext context) {
        if (replayCache == null) {
            logger.warn("No replay cache specified, skipping replay verification");
            return;
        }

        try {
            val messageReplayHandler = new MessageReplaySecurityHandler();
            messageReplayHandler.setExpires(Duration.ofMillis(clockSkewSeconds * 1000));
            messageReplayHandler.setReplayCache(replayCache.get());
            messageReplayHandler.initialize();
            messageReplayHandler.invoke(context.getMessageContext());
        } catch (final ComponentInitializationException e) {
            throw new SAMLException(e);
        } catch (final MessageHandlerException e) {
            throw new SAMLReplayException(e);
        }
    }

    /**
     * Decrypts an EncryptedID, using a decrypter.
     *
     * @param encryptedId The EncryptedID to be decrypted.
     * @param decrypter   The decrypter to use.
     * @return Decrypted ID or {@code null} if any input is {@code null}.
     * @throws SAMLException If the input ID cannot be decrypted.
     */
    protected NameID decryptEncryptedId(final EncryptedID encryptedId, final Decrypter decrypter) throws SAMLException {
        if (encryptedId == null) {
            return null;
        }
        if (decrypter == null) {
            logger.warn("Encrypted attributes returned, but no keystore was provided.");
            return null;
        }

        try {
            logger.debug("Decrypting name id {}", encryptedId);
            val decryptedId = (NameID) decrypter.decrypt(encryptedId);
            return decryptedId;
        } catch (final DecryptionException e) {
            throw new SAMLNameIdDecryptionException("Decryption of an EncryptedID failed.", e);
        }
    }

    /**
     * <p>computeSloKey.</p>
     *
     * @param sessionIndex a {@link String} object
     * @param nameId a {@link SAML2AuthenticationCredentials.SAMLNameID} object
     * @return a {@link String} object
     */
    protected String computeSloKey(final String sessionIndex, final SAML2AuthenticationCredentials.SAMLNameID nameId) {
        if (sessionIndex != null) {
            return sessionIndex;
        }

        if (nameId != null) {
            return nameId.getValue();
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public final void setClockSkewSeconds(final long clockSkewSeconds) {
        this.clockSkewSeconds = clockSkewSeconds;
    }
}
