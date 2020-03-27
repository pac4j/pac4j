package org.pac4j.saml.profile.impl;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.net.URIComparator;
import net.shibboleth.utilities.java.support.net.impl.BasicURLComparator;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.binding.security.impl.MessageReplaySecurityHandler;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Status;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.StatusMessage;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLEndpointMismatchException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLIssueInstantException;
import org.pac4j.saml.exceptions.SAMLIssuerException;
import org.pac4j.saml.exceptions.SAMLNameIdDecryptionException;
import org.pac4j.saml.exceptions.SAMLReplayException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.profile.api.SAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * The abstract class for all SAML response validators.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractSAML2ResponseValidator implements SAML2ResponseValidator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected final URIComparator uriComparator;

    protected final Decrypter decrypter;

    protected final LogoutHandler logoutHandler;

    protected final ReplayCacheProvider replayCache;

    /* maximum skew in seconds between SP and IDP clocks */
    protected int acceptedSkew = 120;

    protected AbstractSAML2ResponseValidator(final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider,
                                             final Decrypter decrypter, final LogoutHandler logoutHandler,
                                             final ReplayCacheProvider replayCache) {
        this(signatureTrustEngineProvider, decrypter, logoutHandler, replayCache, new BasicURLComparator());
    }

    protected AbstractSAML2ResponseValidator(final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider,
                                             final Decrypter decrypter, final LogoutHandler logoutHandler,
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
        String statusValue = status.getStatusCode().getValue();
        if (!StatusCode.SUCCESS.equals(statusValue)) {
            final StatusMessage statusMessage = status.getStatusMessage();
            if (statusMessage != null) {
                statusValue += " / " + statusMessage.getValue();
            }
            throw new SAMLException("Response is not success ; actual " + statusValue);
        }
    }

    protected void validateSignatureIfItExists(final Signature signature, final SAML2MessageContext context,
                                               final SignatureTrustEngine engine) {
        if (signature != null) {
            final String entityId = context.getSAMLPeerEntityContext().getEntityId();
            validateSignature(signature, entityId, engine);
            context.getSAMLPeerEntityContext().setAuthenticated(true);
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
                                     final SignatureTrustEngine trustEngine) {

        final SAMLSignatureProfileValidator validator = new SAMLSignatureProfileValidator();
        try {
            validator.validate(signature);
        } catch (final SignatureException e) {
            throw new SAMLSignatureValidationException("SAMLSignatureProfileValidator failed to validate signature", e);
        }

        final CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
        criteriaSet.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
        criteriaSet.add(new ProtocolCriterion(SAMLConstants.SAML20P_NS));
        criteriaSet.add(new EntityIdCriterion(idpEntityId));
        final boolean valid;
        try {
            valid = trustEngine.validate(signature, criteriaSet);
        } catch (final SecurityException e) {
            throw new SAMLSignatureValidationException("An error occurred during signature validation", e);
        }
        if (!valid) {
            throw new SAMLSignatureValidationException("Signature is not trusted");
        }
    }

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
    protected void validateIssuer(final Issuer issuer, final SAML2MessageContext context) {
        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY)) {
            throw new SAMLIssuerException("Issuer type is not entity but " + issuer.getFormat());
        }

        final String entityId = context.getSAMLPeerEntityContext().getEntityId();
        if (entityId == null || !entityId.equals(issuer.getValue())) {
            throw new SAMLIssuerException("Issuer " + issuer.getValue() + " does not match idp entityId " + entityId);
        }
    }

    protected void validateIssueInstant(final Instant issueInstant) {
        if (!isIssueInstantValid(issueInstant)) {
            throw new SAMLIssueInstantException("Issue instant is too old or in the future");
        }
    }

    protected boolean isIssueInstantValid(final Instant issueInstant) {
        return isDateValid(issueInstant, 0);
    }

    protected boolean isDateValid(final Instant issueInstant, final int interval) {
        final ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        final ZonedDateTime before = now.plusSeconds(acceptedSkew);
        final ZonedDateTime after = now.minusSeconds(acceptedSkew + interval);

        final ZonedDateTime issueInstanceUtc = ZonedDateTime.ofInstant(issueInstant, ZoneOffset.UTC);

        final boolean isDateValid = issueInstanceUtc.isBefore(before) && issueInstanceUtc.isAfter(after);
        if (!isDateValid) {
            logger.warn("interval={},before={},after={},issueInstant={}", interval, before, after, issueInstanceUtc);
        }
        return isDateValid;
    }

    protected void verifyEndpoint(final Endpoint endpoint, final String destination) {
        try {
            if (destination != null && !uriComparator.compare(destination, endpoint.getLocation())
                && !uriComparator.compare(destination, endpoint.getResponseLocation())) {
                throw new SAMLEndpointMismatchException("Intended destination " + destination
                    + " doesn't match any of the endpoint URLs on endpoint "
                    + endpoint.getLocation());
            }
        } catch (final Exception e) {
            throw new SAMLEndpointMismatchException(e);
        }
    }

    protected void verifyMessageReplay(final SAML2MessageContext context) {
        if (replayCache == null) {
            logger.warn("No replay cache specified, skipping replay verification");
            return;
        }

        try {
            final MessageReplaySecurityHandler messageReplayHandler = new MessageReplaySecurityHandler();
            messageReplayHandler.setExpires(Duration.ofMillis(acceptedSkew * 1000));
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
            final NameID decryptedId = (NameID) decrypter.decrypt(encryptedId);
            return decryptedId;
        } catch (final DecryptionException e) {
            throw new SAMLNameIdDecryptionException("Decryption of an EncryptedID failed.", e);
        }
    }

    protected String computeSloKey(final String sessionIndex, final NameID nameId) {
        if (sessionIndex != null) {
            return sessionIndex;
        } else if (nameId != null) {
            return nameId.getValue();
        }

        return null;
    }

    @Override
    public final void setAcceptedSkew(final int acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
    }
}
