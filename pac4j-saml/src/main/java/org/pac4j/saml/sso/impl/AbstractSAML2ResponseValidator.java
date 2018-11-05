package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.net.BasicURLComparator;
import net.shibboleth.utilities.java.support.net.URIComparator;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.core.*;
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
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.*;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract class for all SAML response validators.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public abstract class AbstractSAML2ResponseValidator implements SAML2ResponseValidator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /* maximum skew in seconds between SP and IDP clocks */
    protected int acceptedSkew = 120;

    protected final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    protected final URIComparator uriComparator;

    protected final Decrypter decrypter;


    protected AbstractSAML2ResponseValidator(final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider,
                                             final Decrypter decrypter) {
        this(signatureTrustEngineProvider, decrypter, new BasicURLComparator());
    }

    protected AbstractSAML2ResponseValidator(final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider,
                                             final Decrypter decrypter, final URIComparator uriComparator) {
        this.signatureTrustEngineProvider = signatureTrustEngineProvider;
        this.decrypter = decrypter;
        this.uriComparator = uriComparator;
    }

    /**
     * Validates that the response is a success.
     *
     * @param status the response status.
     */
    protected final void validateSuccess(final Status status) {
        String statusValue = status.getStatusCode().getValue();
        if (!StatusCode.SUCCESS.equals(statusValue)) {
            final StatusMessage statusMessage = status.getStatusMessage();
            if (statusMessage != null) {
                statusValue += " / " + statusMessage.getMessage();
            }
            throw new SAMLException("Response is not success ; actual " + statusValue);
        }
    }

    protected final void validateSignatureIfItExists(final Signature signature, final SAML2MessageContext context,
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
    protected final void validateSignature(final Signature signature, final String idpEntityId,
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

    protected final void validateIssuerIfItExists(final Issuer isser, final SAML2MessageContext context) {
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
    protected final void validateIssuer(final Issuer issuer, final SAML2MessageContext context) {
        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY)) {
            throw new SAMLIssuerException("Issuer type is not entity but " + issuer.getFormat());
        }

        final String entityId = context.getSAMLPeerEntityContext().getEntityId();
        if (entityId == null || !entityId.equals(issuer.getValue())) {
            throw new SAMLIssuerException("Issuer " + issuer.getValue() + " does not match idp entityId " + entityId);
        }
    }

    protected final void validateIssueInstant(final DateTime issueInstant) {
        if (!isIssueInstantValid(issueInstant)) {
            throw new SAMLIssueInstantException("Issue instant is too old or in the future");
        }
    }

    protected final boolean isIssueInstantValid(final DateTime issueInstant) {
        return isDateValid(issueInstant, 0);
    }

    protected final boolean isDateValid(final DateTime issueInstant, final int interval) {
        final DateTime now = DateTime.now(DateTimeZone.UTC);

        final DateTime before = now.plusSeconds(acceptedSkew);
        final DateTime after = now.minusSeconds(acceptedSkew + interval);

        final DateTime issueInstanceUtc = issueInstant.toDateTime(DateTimeZone.UTC);

        final boolean isDateValid = issueInstanceUtc.isBefore(before) && issueInstanceUtc.isAfter(after);
        if (!isDateValid) {
            logger.warn("interval={},before={},after={},issueInstant={}", interval, before.toDateTime(issueInstanceUtc.getZone()),
                after.toDateTime(issueInstanceUtc.getZone()), issueInstanceUtc);
        }
        return isDateValid;
    }

    protected final void verifyEndpoint(final SAML2MessageContext context, final String destination) {
        final Endpoint endpoint = context.getSAMLEndpointContext().getEndpoint();
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

    /**
     * Decrypts an EncryptedID, using a decrypter.
     *
     * @param encryptedId The EncryptedID to be decrypted.
     * @param decrypter   The decrypter to use.
     * @return Decrypted ID or {@code null} if any input is {@code null}.
     * @throws SAMLException If the input ID cannot be decrypted.
     */
    protected final NameID decryptEncryptedId(final EncryptedID encryptedId, final Decrypter decrypter) throws SAMLException {
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

    @Override
    public final void setAcceptedSkew(final int acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
    }
}
