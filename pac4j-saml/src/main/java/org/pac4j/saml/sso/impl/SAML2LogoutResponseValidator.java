package org.pac4j.saml.sso.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
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
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLAssertionAudienceException;
import org.pac4j.saml.exceptions.SAMLAssertionConditionException;
import org.pac4j.saml.exceptions.SAMLEndpointMismatchException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLInResponseToMismatchException;
import org.pac4j.saml.exceptions.SAMLIssueInstantException;
import org.pac4j.saml.exceptions.SAMLIssuerException;
import org.pac4j.saml.exceptions.SAMLNameIdDecryptionException;
import org.pac4j.saml.exceptions.SAMLSignatureRequiredException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.storage.SAMLMessageStorage;
import org.pac4j.saml.util.SAML2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.net.BasicURLComparator;
import net.shibboleth.utilities.java.support.net.URIComparator;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;


/**
 * Validator for SAML logout response
 * 
 * @author Matthieu Taggiasco
 * @since 2.0.0
 */

public class SAML2LogoutResponseValidator implements SAML2ResponseValidator {

    private final static Logger logger = LoggerFactory.getLogger(SAML2LogoutResponseValidator.class);

    /* maximum skew in seconds between SP and IDP clocks */
    private int acceptedSkew = 120;

    private final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider;

    private final URIComparator uriComparator;


    public SAML2LogoutResponseValidator(final SAML2SignatureTrustEngineProvider engine) {
        this(engine, new BasicURLComparator());
    }

    public SAML2LogoutResponseValidator(final SAML2SignatureTrustEngineProvider engine, final URIComparator uriComparator) {
        this.signatureTrustEngineProvider = engine;
        this.uriComparator = uriComparator;
    }

    /**
     * Validates the SAML protocol response and the SAML SSO response.
     * The method decrypt encrypted assertions if any.
     *
     * @param context the context
     */
    @Override
    public Credentials validate(final SAML2MessageContext context) {

        final SAMLObject message = context.getMessage();

        if (!(message instanceof Response)) {
            throw new SAMLException("Response instance is an unsupported type");
        }
        final Response response = (Response) message;
        final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
        validateSamlProtocolResponse(response, context, engine);

        return null;
    }

    /**
     * Validates the SAML protocol response:
     *  - IssueInstant
     *  - Issuer
     *  - StatusCode
     *  - Signature
     *
     * @param response the response
     * @param context the context
     * @param engine the engine
     */
    protected final void validateSamlProtocolResponse(final Response response, final SAML2MessageContext context,
                                                      final SignatureTrustEngine engine) {

        if (!StatusCode.SUCCESS.equals(response.getStatus().getStatusCode().getValue())) {
            String status = response.getStatus().getStatusCode().getValue();
            if (response.getStatus().getStatusMessage() != null) {
                status += " / " + response.getStatus().getStatusMessage().getMessage();
            }
            throw new SAMLException("Logout response is not success ; actual " + status);
        }

        if (response.getSignature() != null) {
            final String entityId = context.getSAMLPeerEntityContext().getEntityId();
            validateSignature(response.getSignature(), entityId, engine);
            context.getSAMLPeerEntityContext().setAuthenticated(true);
        }

        if (!isIssueInstantValid(response.getIssueInstant())) {
            throw new SAMLIssueInstantException("Response issue instant is too old or in the future");
        }
        
        final SAMLMessageStorage messageStorage = context.getSAMLMessageStorage();
        if (messageStorage != null && response.getInResponseTo() != null) {
            final XMLObject xmlObject = messageStorage.retrieveMessage(response.getInResponseTo());
            if (xmlObject == null) {
                throw new SAMLInResponseToMismatchException("InResponseToField of the Response doesn't correspond to sent message "
                    + response.getInResponseTo());
            } else if (!(xmlObject instanceof LogoutRequest)) {
                throw new SAMLInResponseToMismatchException("Sent request was of different type than the expected LogoutRequest "
                    + response.getInResponseTo());
            }
        }

        verifyEndpoint(context.getSAMLEndpointContext().getEndpoint(), response.getDestination());
        if (response.getIssuer() != null) {
            validateIssuer(response.getIssuer(), context);
        }
    }

    protected final void verifyEndpoint(final Endpoint endpoint, final String destination) {
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
     * Validate issuer format and value.
     *
     * @param issuer the issuer
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


    /**
     * Decrypts an EncryptedID, using a decrypter.
     * 
     * @param encryptedId The EncryptedID to be decrypted.
     * @param decrypter The decrypter to use.
     * 
     * @return Decrypted ID or {@code null} if any input is {@code null}.
     * 
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

    /**
     * Validate Bearer subject confirmation data
     *  - notBefore
     *  - NotOnOrAfter
     *  - recipient
     *
     * @param data the data
     * @param context the context
     * @return true if all Bearer subject checks are passing
     */
    protected final boolean isValidBearerSubjectConfirmationData(final SubjectConfirmationData data,
                                                                 final SAML2MessageContext context) {
        if (data == null) {
            logger.debug("SubjectConfirmationData cannot be null for Bearer confirmation");
            return false;
        }

        if (data.getNotBefore() != null) {
            logger.debug("SubjectConfirmationData notBefore must be null for Bearer confirmation");
            return false;
        }

        if (data.getNotOnOrAfter() == null) {
            logger.debug("SubjectConfirmationData notOnOrAfter cannot be null for Bearer confirmation");
            return false;
        }

        if (data.getNotOnOrAfter().plusSeconds(acceptedSkew).isBeforeNow()) {
            logger.debug("SubjectConfirmationData notOnOrAfter is too old");
            return false;
        }

        try {
            if (data.getRecipient() == null) {
                logger.debug("SubjectConfirmationData recipient cannot be null for Bearer confirmation");
                return false;
            } else {
                final Endpoint endpoint = context.getSAMLEndpointContext().getEndpoint();
                if (endpoint == null) {
                    logger.warn("No endpoint was found in the SAML endpoint context");
                    return false;
                }
    
                final URI recipientUri = new URI(data.getRecipient());
                final URI appEndpointUri = new URI(endpoint.getLocation());
                if (!SAML2Utils.urisEqualAfterPortNormalization(recipientUri, appEndpointUri)) {
                    logger.debug("SubjectConfirmationData recipient {} does not match SP assertion consumer URL, found. " 
                        + "SP ACS URL from context: {}", recipientUri, appEndpointUri);
                    return false;
                }
            }
        } catch (URISyntaxException use) {
            logger.error("Unable to check SubjectConfirmationData recipient, a URI has invalid syntax.", use);
            return false;
        }
        
        return true;
    }

    /**
     * Validate assertionConditions
     *  - notBefore
     *  - notOnOrAfter
     *
     * @param conditions the conditions
     * @param context the context
     */
    protected final void validateAssertionConditions(final Conditions conditions, final SAML2MessageContext context) {

        if (conditions == null) {
            throw new SAMLAssertionConditionException("Assertion conditions cannot be null");
        }

        if (conditions.getNotBefore() != null && conditions.getNotBefore().minusSeconds(acceptedSkew).isAfterNow()) {
            throw new SAMLAssertionConditionException("Assertion condition notBefore is not valid");
        }

        if (conditions.getNotOnOrAfter() != null && conditions.getNotOnOrAfter().plusSeconds(acceptedSkew).isBeforeNow()) {
            throw new SAMLAssertionConditionException("Assertion condition notOnOrAfter is not valid");
        }

        final String entityId = context.getSAMLSelfEntityContext().getEntityId();
        validateAudienceRestrictions(conditions.getAudienceRestrictions(), entityId);
    }

    /**
     * Validate audience by matching the SP entityId.
     *
     * @param audienceRestrictions the audience restrictions
     * @param spEntityId the sp entity id
     */
    protected final void validateAudienceRestrictions(final List<AudienceRestriction> audienceRestrictions,
                                                      final String spEntityId) {

        if (audienceRestrictions == null || audienceRestrictions.isEmpty()) {
            throw new SAMLException("Audience restrictions cannot be null or empty");
        }

        final Set<String> audienceUris = new HashSet<String>();
        for (final AudienceRestriction audienceRestriction : audienceRestrictions) {
            if (audienceRestriction.getAudiences() != null) {
                for (final Audience audience : audienceRestriction.getAudiences()) {
                    audienceUris.add(audience.getAudienceURI());
                }
            }
        }
        if (!audienceUris.contains(spEntityId)) {
            throw new SAMLAssertionAudienceException("Assertion audience " + audienceUris + " does not match SP configuration "
                    + spEntityId);
        }
    }

    /**
     * Validate assertion signature. If none is found and the SAML response did not have one and the SP requires
     * the assertions to be signed, the validation fails.
     *
     * @param signature the signature
     * @param context the context
     * @param engine the engine
     */
    protected final void validateAssertionSignature(final Signature signature, final SAML2MessageContext context,
                                                    final SignatureTrustEngine engine) {

        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();

        if (signature != null) {
            final String entityId = peerContext.getEntityId();
            validateSignature(signature, entityId, engine);
        } else {
            if (!peerContext.isAuthenticated()) {
                throw new SAMLSignatureRequiredException("Assertion or response must be signed");
            }
        }
    }

    /**
     * Validate the given digital signature by checking its profile and value.
     *
     * @param signature the signature
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

    private boolean isDateValid(final DateTime issueInstant, final int interval) {
        final DateTime before =  DateTime.now(DateTimeZone.UTC).plusSeconds(acceptedSkew);
        final DateTime after =  DateTime.now(DateTimeZone.UTC).minusSeconds(acceptedSkew + interval);
        final DateTime issueInstanceUtc = issueInstant.toDateTime(DateTimeZone.UTC);
        boolean isDateValid = issueInstanceUtc.isBefore(before) && issueInstanceUtc.isAfter(after);
        if (!isDateValid) {
            logger.trace("interval={},before={},after={},issueInstant={}", interval, before.toDateTime(issueInstanceUtc.getZone()),
                after.toDateTime(issueInstanceUtc.getZone()), issueInstanceUtc);
        }
        return isDateValid;
    }

    private boolean isIssueInstantValid(final DateTime issueInstant) {
        return isDateValid(issueInstant, 0);
    }

    @Override
    public final void setAcceptedSkew(final int acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
    }

    @Override
    public final void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
    }
}
