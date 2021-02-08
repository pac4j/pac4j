package org.pac4j.saml.sso.impl;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.BaseID;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.EncryptedAttribute;
import org.opensaml.saml.saml2.core.EncryptedID;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.encryption.Decrypter;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMAssertionSubjectException;
import org.pac4j.saml.exceptions.SAMLAssertionAudienceException;
import org.pac4j.saml.exceptions.SAMLAssertionConditionException;
import org.pac4j.saml.exceptions.SAMLAuthnContextClassRefException;
import org.pac4j.saml.exceptions.SAMLAuthnInstantException;
import org.pac4j.saml.exceptions.SAMLAuthnSessionCriteriaException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLInResponseToMismatchException;
import org.pac4j.saml.exceptions.SAMLReplayException;
import org.pac4j.saml.exceptions.SAMLSignatureRequiredException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.exceptions.SAMLSubjectConfirmationException;
import org.pac4j.saml.profile.impl.AbstractSAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.store.SAMLMessageStore;
import org.pac4j.saml.util.Configuration;
import org.pac4j.saml.util.SAML2Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Class responsible for executing every required checks for validating a SAML response.
 * The method validate populates the given {@link SAML2MessageContext}
 * with the correct SAML assertion and the corresponding nameID's Bearer subject if every checks succeeds.
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.5.0
 */
public class SAML2AuthnResponseValidator extends AbstractSAML2ResponseValidator {

    private final SAML2Configuration saml2Configuration;

    public SAML2AuthnResponseValidator(
        final SAML2SignatureTrustEngineProvider engine,
        final Decrypter decrypter,
        final ReplayCacheProvider replayCache,
        final SAML2Configuration saml2Configuration) {
        super(engine, decrypter, saml2Configuration.getLogoutHandler(), replayCache, saml2Configuration.getUriComparator());
        this.saml2Configuration = saml2Configuration;
    }

    @Override
    public Credentials validate(final SAML2MessageContext context) {

        final SAMLObject message = (SAMLObject) context.getMessageContext().getMessage();

        if (!(message instanceof Response)) {
            throw new SAMLException("Must be a Response type");
        }

        final Response response = (Response) message;
        final SignatureTrustEngine engine = this.signatureTrustEngineProvider.build();
        verifyMessageReplay(context);
        validateSamlProtocolResponse(response, context, engine);

        if (decrypter != null) {
            decryptEncryptedAssertions(response, decrypter);
        }

        validateSamlSSOResponse(response, context, engine, decrypter);
        return buildSAML2Credentials(context);
    }

    protected SAML2Credentials buildSAML2Credentials(final SAML2MessageContext context) {
        final Assertion subjectAssertion = context.getSubjectAssertion();

        final List<Attribute> attributes = collectAssertionAttributes(subjectAssertion);
        final SAML2Credentials.SAMLNameID samlNameId = determineNameID(context, SAML2Credentials.SAMLAttribute.from(attributes));
        final String sessionIndex = getSessionIndex(subjectAssertion);
        final String sloKey = computeSloKey(sessionIndex, samlNameId);
        if (sloKey != null) {
            logoutHandler.recordSession(context.getWebContext(), context.getSessionStore(), sloKey);
        }

        final String issuerEntityId = subjectAssertion.getIssuer().getValue();
        final List<AuthnStatement> authnStatements = subjectAssertion.getAuthnStatements();
        final List<String> authnContexts = new ArrayList<>();
        for (final AuthnStatement authnStatement : authnStatements) {
            if (authnStatement.getAuthnContext().getAuthnContextClassRef() != null) {
                authnContexts.add(authnStatement.getAuthnContext().getAuthnContextClassRef().getURI());
            }
        }
        return new SAML2Credentials(samlNameId, issuerEntityId, SAML2Credentials.SAMLAttribute.from(attributes),
            subjectAssertion.getConditions(), sessionIndex, authnContexts);
    }

    protected List<Attribute> collectAssertionAttributes(final Assertion subjectAssertion) {
        final List<Attribute> attributes = new ArrayList<>();
        for (final AttributeStatement attributeStatement : subjectAssertion.getAttributeStatements()) {
            for (final Attribute attribute : attributeStatement.getAttributes()) {
                attributes.add(attribute);
            }
            if (!attributeStatement.getEncryptedAttributes().isEmpty()) {
                if (decrypter == null) {
                    logger.warn("Encrypted attributes returned, but no keystore was provided.");
                } else {
                    for (final EncryptedAttribute encryptedAttribute : attributeStatement.getEncryptedAttributes()) {
                        try {
                            attributes.add(decrypter.decrypt(encryptedAttribute));
                        } catch (final DecryptionException e) {
                            logger.warn("Decryption of attribute failed, continue with the next one", e);
                        }
                    }
                }
            }
        }
        return attributes;
    }

    protected SAML2Credentials.SAMLNameID determineNameID(final SAML2MessageContext context,
                                                          final List<SAML2Credentials.SAMLAttribute> attributes) {
        if (saml2Configuration.getNameIdAttribute() != null) {
            final Optional<SAML2Credentials.SAMLNameID> nameId = attributes
                .stream()
                .filter(attribute -> attribute.getName().equals(saml2Configuration.getNameIdAttribute()))
                .findFirst()
                .map(SAML2Credentials.SAMLNameID::from);
            if (nameId.isPresent()) {
                return nameId.get();
            }
        }
        final NameID nameId = Objects.requireNonNull(context.getSAMLSubjectNameIdentifierContext().getSAML2SubjectNameID());
        return SAML2Credentials.SAMLNameID.from(nameId);
    }

    /**
     * Searches the sessionIndex in the assertion
     *
     * @param subjectAssertion assertion from the response
     * @return the sessionIndex if found in the assertion
     */
    protected String getSessionIndex(final Assertion subjectAssertion) {
        final List<AuthnStatement> authnStatements = subjectAssertion.getAuthnStatements();
        if (authnStatements != null && !authnStatements.isEmpty()) {
            final AuthnStatement statement = authnStatements.get(0);
            if (statement != null) {
                return statement.getSessionIndex();
            }
        }
        return null;
    }

    /**
     * Validates the SAML protocol response:
     * - IssueInstant
     * - Issuer
     * - StatusCode
     * - Signature
     *
     * @param response the response
     * @param context  the context
     * @param engine   the engine
     */
    protected void validateSamlProtocolResponse(final Response response, final SAML2MessageContext context,
                                                final SignatureTrustEngine engine) {

        validateSuccess(response.getStatus());

        validateInResponseTo(response);

        if (!response.getVersion().equals(SAMLVersion.VERSION_20)) {
            throw new SAMLException("Invalid SAML version assigned to the response " + response.getVersion());
        }

        if (saml2Configuration.isWantsResponsesSigned() && response.getSignature() == null) {
            logger.debug(
                "Unable to find a signature on the SAML response returned. Pac4j is configured to enforce "
                    + "signatures on SAML2 responses from identity providers and the returned response\n{}\n"
                    + "does not contain any signature",
                Configuration.serializeSamlObject(response));

            throw new SAMLSignatureValidationException("Unable to find a signature on the SAML response returned");
        }

        if (saml2Configuration.isWantsResponsesSigned()) {
            validateSignatureIfItExists(response.getSignature(), context, engine);
        }

        validateIssueInstant(response.getIssueInstant());

        AuthnRequest request = null;
        final SAMLMessageStore messageStorage = context.getSAMLMessageStore();
        if (messageStorage != null && response.getInResponseTo() != null) {
            final Optional<XMLObject> xmlObject = messageStorage.get(response.getInResponseTo());
            if (xmlObject.isEmpty()) {
                throw new SAMLInResponseToMismatchException(
                    "InResponseToField of the Response doesn't correspond to sent message "
                        + response.getInResponseTo());
            } else if (xmlObject.get() instanceof AuthnRequest) {
                request = (AuthnRequest) xmlObject.get();
            } else {
                throw new SAMLInResponseToMismatchException(
                    "Sent request was of different type than the expected AuthnRequest "
                        + response.getInResponseTo());
            }
        }

        final Endpoint endpoint = Objects.requireNonNull(context.getSAMLEndpointContext().getEndpoint());

        final List<String> expected = new ArrayList<>();
        if (endpoint.getLocation() != null) {
            expected.add(endpoint.getLocation());
        }
        if (endpoint.getResponseLocation() != null) {
            expected.add(endpoint.getResponseLocation());
        }
        verifyEndpoint(expected, response.getDestination());
        if (request != null) {
            verifyRequest(request, context);
        }

        validateIssuerIfItExists(response.getIssuer(), context);
    }

    protected void validateInResponseTo(final Response response) {
        if (StringUtils.isEmpty(response.getInResponseTo())) {
            throw new SAMLInResponseToMismatchException("Missing InResponseTo field");
        }
    }

    protected void verifyRequest(final AuthnRequest request, final SAML2MessageContext context) {
        // Verify endpoint requested in the original request
        final AssertionConsumerService assertionConsumerService = (AssertionConsumerService) context.
            getSAMLEndpointContext()
            .getEndpoint();
        if (request.getAssertionConsumerServiceIndex() != null) {
            if (!request.getAssertionConsumerServiceIndex().equals(assertionConsumerService.getIndex())) {
                logger.warn("Response was received at a different endpoint index than was requested");
            }
        } else {
            final String requestedResponseURL = request.getAssertionConsumerServiceURL();
            final String requestedBinding = request.getProtocolBinding();
            if (requestedResponseURL != null) {
                final String responseLocation;
                if (assertionConsumerService.getResponseLocation() != null) {
                    responseLocation = assertionConsumerService.getResponseLocation();
                } else {
                    responseLocation = assertionConsumerService.getLocation();
                }
                if (!requestedResponseURL.equals(responseLocation)) {
                    logger.warn("Response was received at a different endpoint URL {} than was requested {}",
                        responseLocation, requestedResponseURL);
                }
            }
            if (requestedBinding != null && !requestedBinding.equals(context.getSAMLBindingContext().getBindingUri())) {
                logger.warn("Response was received using a different binding {} than was requested {}",
                    context.getSAMLBindingContext().getBindingUri(), requestedBinding);
            }
        }
    }

    /**
     * Validates the SAML SSO response by finding a valid assertion with authn statements.
     * Populates the {@link SAML2MessageContext} with a subjectAssertion and a subjectNameIdentifier.
     *
     * @param response  the response
     * @param context   the context
     * @param engine    the engine
     * @param decrypter the decrypter
     */
    protected void validateSamlSSOResponse(final Response response, final SAML2MessageContext context,
                                           final SignatureTrustEngine engine, final Decrypter decrypter) {

        final List<SAMLException> errors = new ArrayList<>();
        for (final Assertion assertion : response.getAssertions()) {
            if (!assertion.getAuthnStatements().isEmpty()) {
                try {
                    validateAssertion(assertion, context, engine, decrypter);
                } catch (final SAMLException e) {
                    logger.error("Current assertion validation failed, continue with the next one", e);
                    errors.add(e);
                    continue;
                }
                context.setSubjectAssertion(assertion);
                break;
            }
        }

        if (!errors.isEmpty()) {
            throw errors.get(0);
        }
        if (context.getSubjectAssertion() == null) {
            throw new SAMAssertionSubjectException("No valid subject assertion found in response");
        }

        // We do not check EncryptedID here because it has been already decrypted and stored into NameID
        final List<SubjectConfirmation> subjectConfirmations = context.getSubjectConfirmations();

        if (subjectConfirmations == null || subjectConfirmations.isEmpty()) {
            if (saml2Configuration.getNameIdAttribute() != null) {
                logger.debug("NameID will be determined from attribute {}", saml2Configuration.getNameIdAttribute());
            } else {
                final NameID nameIdentifier = (NameID) context.getSAMLSubjectNameIdentifierContext().getSubjectNameIdentifier();
                if ((nameIdentifier == null || nameIdentifier.getValue() == null) && context.getBaseID() == null
                    && (subjectConfirmations == null || subjectConfirmations.isEmpty())) {
                    throw new SAMLException(
                        "Subject NameID, BaseID and EncryptedID cannot be all null at the same time without Subject Confirmations.");
                }
            }
        }
    }

    /**
     * Decrypt encrypted assertions and add them to the assertions list of the response.
     *
     * @param response  the response
     * @param decrypter the decrypter
     */
    protected void decryptEncryptedAssertions(final Response response, final Decrypter decrypter) {
        for (final EncryptedAssertion encryptedAssertion : response.getEncryptedAssertions()) {
            try {
                final Assertion decryptedAssertion = decrypter.decrypt(encryptedAssertion);
                response.getAssertions().add(decryptedAssertion);
            } catch (final DecryptionException e) {
                logger.error("Decryption of assertion failed, continue with the next one", e);
            }
        }

    }

    /**
     * Validate the given assertion:
     * - issueInstant
     * - issuer
     * - subject
     * - conditions
     * - authnStatements
     * - signature
     *
     * @param assertion the assertion
     * @param context   the context
     * @param engine    the engine
     * @param decrypter the decrypter
     */
    protected void validateAssertion(final Assertion assertion, final SAML2MessageContext context,
                                     final SignatureTrustEngine engine, final Decrypter decrypter) {

        if (!assertion.getVersion().equals(SAMLVersion.VERSION_20)) {
            throw new SAMLException("Invalid SAML assertion version");
        }

        validateIssueInstant(assertion.getIssueInstant());

        validateIssuer(assertion.getIssuer(), context);

        if (assertion.getSubject() != null) {
            validateSubject(assertion.getSubject(), context, decrypter);
        } else {
            throw new SAMAssertionSubjectException("Assertion subject cannot be null");
        }

        validateAssertionConditions(assertion.getConditions(), context);

        validateAuthenticationStatements(assertion.getAuthnStatements(), context);

        validateAssertionSignature(assertion.getSignature(), context, engine);
    }

    /**
     * Validate the given subject by finding a valid Bearer confirmation. If the subject is valid, put its nameID in the
     * context.
     * <p>
     * NameID / BaseID / EncryptedID is first looked up directly in the Subject. If not present there, then all relevant
     * SubjectConfirmations are parsed and the IDs are taken from them.
     *
     * @param subject   The Subject from an assertion.
     * @param context   SAML message context.
     * @param decrypter Decrypter used to decrypt some encrypted IDs, if they are present.
     *                  May be {@code null}, no decryption will be possible then.
     */
    @SuppressWarnings("unchecked")
    protected void validateSubject(final Subject subject, final SAML2MessageContext context,
                                   final Decrypter decrypter) {
        boolean samlIDFound = false;

        // Read NameID/BaseID/EncryptedID from the subject. If not present directly in the subject, try to find it in subject confirmations.
        NameID nameIdFromSubject = subject.getNameID();
        final BaseID baseIdFromSubject = subject.getBaseID();
        final EncryptedID encryptedIdFromSubject = subject.getEncryptedID();

        // Encrypted ID can overwrite the non-encrypted one, if present
        final NameID decryptedNameIdFromSubject = decryptEncryptedId(encryptedIdFromSubject, decrypter);
        if (decryptedNameIdFromSubject != null) {
            nameIdFromSubject = decryptedNameIdFromSubject;
        }

        // If we have a Name ID or a Base ID, we are fine
        // If we don't have anything, let's go through all subject confirmations and get the IDs from them.
        // At least one should be present but we don't care at this point.
        if (nameIdFromSubject != null || baseIdFromSubject != null) {
            context.getSAMLSubjectNameIdentifierContext().setSubjectNameIdentifier(nameIdFromSubject);
            context.setBaseID(baseIdFromSubject);
            samlIDFound = true;
        }

        for (final SubjectConfirmation confirmation : subject.getSubjectConfirmations()) {
            if (SubjectConfirmation.METHOD_BEARER.equals(confirmation.getMethod())
                && isValidBearerSubjectConfirmationData(confirmation.getSubjectConfirmationData(), context)) {
                validateAssertionReplay((Assertion) subject.getParent(), confirmation.getSubjectConfirmationData());
                NameID nameIDFromConfirmation = confirmation.getNameID();
                final BaseID baseIDFromConfirmation = confirmation.getBaseID();
                final EncryptedID encryptedIDFromConfirmation = confirmation.getEncryptedID();

                // Encrypted ID can overwrite the non-encrypted one, if present
                final NameID decryptedNameIdFromConfirmation = decryptEncryptedId(encryptedIDFromConfirmation,
                    decrypter);
                if (decryptedNameIdFromConfirmation != null) {
                    nameIDFromConfirmation = decryptedNameIdFromConfirmation;
                }

                if (!samlIDFound && (nameIDFromConfirmation != null || baseIDFromConfirmation != null)) {
                    context.getSAMLSubjectNameIdentifierContext().setSubjectNameIdentifier(nameIDFromConfirmation);
                    context.setBaseID(baseIDFromConfirmation);
                    context.getSubjectConfirmations().add(confirmation);
                    samlIDFound = true;
                }
                if (!samlIDFound) {
                    logger.warn(
                        "Could not find any Subject NameID/BaseID/EncryptedID, neither directly in the Subject nor in any Subject "
                            + "Confirmation.");
                }
                return;
            }
        }

        throw new SAMLSubjectConfirmationException("Subject confirmation validation failed");
    }

    /**
     * Validate Bearer subject confirmation data
     * - notBefore
     * - NotOnOrAfter
     * - recipient
     *
     * @param data    the data
     * @param context the context
     * @return true if all Bearer subject checks are passing
     */
    protected boolean isValidBearerSubjectConfirmationData(final SubjectConfirmationData data,
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

        final Instant now = ZonedDateTime.now(ZoneOffset.UTC).toInstant();
        final boolean expired = data.getNotOnOrAfter().plusSeconds(acceptedSkew).isBefore(now);

        if (expired) {
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
                    logger.debug(
                        "SubjectConfirmationData recipient {} does not match SP assertion consumer URL, found. "
                            + "SP ACS URL from context: {}", recipientUri, appEndpointUri);
                    return false;
                }
            }
        } catch (final URISyntaxException use) {
            logger.error("Unable to check SubjectConfirmationData recipient, a URI has invalid syntax.", use);
            return false;
        }

        return true;
    }

    /**
     * Checks that the bearer assertion is not being replayed.
     *
     * @param assertion The Assertion to check
     * @param data      The SubjectConfirmationData to check the assertion against
     */
    protected void validateAssertionReplay(final Assertion assertion, final SubjectConfirmationData data) {
        if (assertion.getID() == null) {
            throw new SAMLReplayException("The assertion does not have an ID");
        }

        if (replayCache == null) {
            logger.warn("No replay cache specified, skipping replay verification");
            return;
        }

        final Instant expires = Instant.ofEpochMilli(data.getNotOnOrAfter().toEpochMilli() + acceptedSkew * 1000);
        if (!replayCache.get().check(getClass().getName(), assertion.getID(), expires)) {
            throw new SAMLReplayException("Rejecting replayed assertion ID '" + assertion.getID() + "'");
        }
    }

    /**
     * Validate assertionConditions
     * - notBefore
     * - notOnOrAfter
     *
     * @param conditions the conditions
     * @param context    the context
     */
    protected void validateAssertionConditions(final Conditions conditions, final SAML2MessageContext context) {

        if (conditions == null) {
            return;
        }

        final Instant now = ZonedDateTime.now(ZoneOffset.UTC).toInstant();
        if (conditions.getNotBefore() != null) {
            final boolean expired = conditions.getNotBefore().minusSeconds(acceptedSkew).isAfter(now);
            if (expired) {
                throw new SAMLAssertionConditionException("Assertion condition notBefore is not valid");
            }
        }

        if (conditions.getNotOnOrAfter() != null) {
            final boolean expired = conditions.getNotOnOrAfter().plusSeconds(acceptedSkew).isBefore(now);
            if (expired) {
                throw new SAMLAssertionConditionException("Assertion condition notOnOrAfter is not valid");
            }
        }

        final String entityId = context.getSAMLSelfEntityContext().getEntityId();
        validateAudienceRestrictions(conditions.getAudienceRestrictions(), entityId);
    }

    /**
     * Validate audience by matching the SP entityId.
     *
     * @param audienceRestrictions the audience restrictions
     * @param spEntityId           the sp entity id
     */
    protected void validateAudienceRestrictions(final List<AudienceRestriction> audienceRestrictions,
                                                final String spEntityId) {

        if (audienceRestrictions == null || audienceRestrictions.isEmpty()) {
            throw new SAMLAssertionAudienceException("Audience restrictions cannot be null or empty");
        }

        final Set<String> audienceUris = new HashSet<>();
        for (final AudienceRestriction audienceRestriction : audienceRestrictions) {
            if (audienceRestriction.getAudiences() != null) {
                for (final Audience audience : audienceRestriction.getAudiences()) {
                    audienceUris.add(audience.getURI());
                }
            }
        }
        if (!audienceUris.contains(spEntityId)) {
            throw new SAMLAssertionAudienceException("Assertion audience " + audienceUris
                + " does not match SP configuration " + spEntityId);
        }
    }

    /**
     * Validate the given authnStatements:
     * - authnInstant
     * - sessionNotOnOrAfter
     *
     * @param authnStatements the authn statements
     * @param context         the context
     */
    protected void validateAuthenticationStatements(final List<AuthnStatement> authnStatements,
                                                    final SAML2MessageContext context) {
        final List<String> authnClassRefs = new ArrayList<>();
        final Instant now = ZonedDateTime.now(ZoneOffset.UTC).toInstant();
        for (final AuthnStatement statement : authnStatements) {
            if (!isAuthnInstantValid(statement.getAuthnInstant())) {
                throw new SAMLAuthnInstantException("Authentication issue instant is too old or in the future");
            }
            if (statement.getSessionNotOnOrAfter() != null) {
                final boolean expired = statement.getSessionNotOnOrAfter().isBefore(now);
                if (expired) {
                    throw new SAMLAuthnSessionCriteriaException("Authentication session between IDP and subject has ended");
                }
            }
            authnClassRefs.add(statement.getAuthnContext().getAuthnContextClassRef().getURI());
        }

        validateAuthnContextClassRefs(authnClassRefs);
    }

    protected void validateAuthnContextClassRefs(final List<String> providedAuthnContextClassRefs) {
        if (!saml2Configuration.getAuthnContextClassRefs().isEmpty()) {
            logger.debug("Required authentication context class refs are {}", saml2Configuration.getAuthnContextClassRefs());
            logger.debug("Found authentication context class refs are {}", providedAuthnContextClassRefs);

            final Collection<String> results = CollectionUtils.intersection(
                saml2Configuration.getAuthnContextClassRefs(), providedAuthnContextClassRefs);
            if (results.size() != saml2Configuration.getAuthnContextClassRefs().size()) {
                throw new SAMLAuthnContextClassRefException("Requested authentication context class refs do not match "
                    + " those in authentication statements from IDP.");
            }
        }
    }

    /**
     * Validate assertion signature. If none is found and the SAML response did not have one and the SP requires
     * the assertions to be signed, the validation fails.
     *
     * @param signature the signature
     * @param context   the context
     * @param engine    the engine
     */
    protected void validateAssertionSignature(final Signature signature, final SAML2MessageContext context,
                                              final SignatureTrustEngine engine) {

        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();

        if (signature != null) {
            final String entityId = peerContext.getEntityId();
            validateSignature(signature, entityId, engine);
        } else {
            if (wantsAssertionsSigned(context)) {
                throw new SAMLSignatureRequiredException("Assertion must be explicitly signed");
            }
            if (!peerContext.isAuthenticated() && !saml2Configuration.isAllSignatureValidationDisabled()) {
                throw new SAMLSignatureRequiredException("Unauthenticated response contains an unsigned assertion");
            }
        }
    }

    @VisibleForTesting
    Boolean wantsAssertionsSigned(final SAML2MessageContext context) {
        if (context == null) {
            return saml2Configuration.isWantsAssertionsSigned();
        }
        final SPSSODescriptor spDescriptor = context.getSPSSODescriptor();
        if (spDescriptor == null) {
            return saml2Configuration.isWantsAssertionsSigned();
        }
        return spDescriptor.getWantAssertionsSigned();
    }

    private boolean isAuthnInstantValid(final Instant authnInstant) {
        return isDateValid(authnInstant, saml2Configuration.getMaximumAuthenticationLifetime());
    }
}
