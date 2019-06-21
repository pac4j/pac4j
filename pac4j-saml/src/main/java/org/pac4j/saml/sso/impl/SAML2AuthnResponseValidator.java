package org.pac4j.saml.sso.impl;

import com.google.common.annotations.VisibleForTesting;
import net.shibboleth.utilities.java.support.net.BasicURLComparator;
import net.shibboleth.utilities.java.support.net.URIComparator;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
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
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.credentials.SAML2Credentials;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMAssertionSubjectException;
import org.pac4j.saml.exceptions.SAMLAssertionAudienceException;
import org.pac4j.saml.exceptions.SAMLAssertionConditionException;
import org.pac4j.saml.exceptions.SAMLAuthnInstantException;
import org.pac4j.saml.exceptions.SAMLAuthnSessionCriteriaException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLInResponseToMismatchException;
import org.pac4j.saml.exceptions.SAMLReplayException;
import org.pac4j.saml.exceptions.SAMLSignatureRequiredException;
import org.pac4j.saml.exceptions.SAMLSubjectConfirmationException;
import org.pac4j.saml.profile.impl.AbstractSAML2ResponseValidator;
import org.pac4j.saml.replay.ReplayCacheProvider;
import org.pac4j.saml.storage.SAMLMessageStorage;
import org.pac4j.saml.util.SAML2Utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    /* maximum lifetime after a successful authentication on an IDP */
    private int maximumAuthenticationLifetime;

    private final boolean wantsAssertionsSigned;
    
    /**
     * @deprecated this constructor does not accept a replay cache, replay protection will be disabled
     */
    @Deprecated
    public SAML2AuthnResponseValidator(final SAML2SignatureTrustEngineProvider engine,
            final Decrypter decrypter,
            final LogoutHandler logoutHandler,
            final int maximumAuthenticationLifetime,
            final boolean wantsAssertionsSigned) {
        this(engine, decrypter, logoutHandler, maximumAuthenticationLifetime, wantsAssertionsSigned, null, new BasicURLComparator());
    }

    /**
     * @deprecated this constructor does not accept a replay cache, replay protection will be disabled
     */
    @Deprecated
    public SAML2AuthnResponseValidator(final SAML2SignatureTrustEngineProvider engine,
            final Decrypter decrypter,
            final LogoutHandler logoutHandler,
            final int maximumAuthenticationLifetime,
            final boolean wantsAssertionsSigned,
            final URIComparator uriComparator) {
        this(engine, decrypter, logoutHandler, maximumAuthenticationLifetime, wantsAssertionsSigned, null, uriComparator);
    }

    public SAML2AuthnResponseValidator(final SAML2SignatureTrustEngineProvider engine,
                                       final Decrypter decrypter,
                                       final LogoutHandler logoutHandler,
                                       final int maximumAuthenticationLifetime,
                                       final boolean wantsAssertionsSigned,
                                       final ReplayCacheProvider replayCache) {
        this(engine, decrypter, logoutHandler, maximumAuthenticationLifetime, wantsAssertionsSigned, replayCache, new BasicURLComparator());
    }

    public SAML2AuthnResponseValidator(final SAML2SignatureTrustEngineProvider engine,
                                       final Decrypter decrypter,
                                       final LogoutHandler logoutHandler,
                                       final int maximumAuthenticationLifetime,
                                       final boolean wantsAssertionsSigned,
                                       final ReplayCacheProvider replayCache,
                                       final URIComparator uriComparator) {
        super(engine, decrypter, logoutHandler, replayCache, uriComparator);
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
        this.wantsAssertionsSigned = wantsAssertionsSigned;
    }

    @Override
    public Credentials validate(final SAML2MessageContext context) {

        final SAMLObject message = context.getMessage();

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

    protected final SAML2Credentials buildSAML2Credentials(final SAML2MessageContext context) {

        final NameID nameId = context.getSAMLSubjectNameIdentifierContext().getSAML2SubjectNameID();
        final Assertion subjectAssertion = context.getSubjectAssertion();

        final String sessionIndex = getSessionIndex(subjectAssertion);
        final String sloKey = computeSloKey(sessionIndex, nameId);
        if (sloKey != null) {
            logoutHandler.recordSession(context.getWebContext(), sloKey);
        }

        final String issuerEntityId = subjectAssertion.getIssuer().getValue();
        final List<AuthnStatement> authnStatements = subjectAssertion.getAuthnStatements();
        final List<String> authnContexts = new ArrayList<>();
        for (final AuthnStatement authnStatement : authnStatements) {
            if(authnStatement.getAuthnContext().getAuthnContextClassRef() != null) {
                authnContexts.add(authnStatement.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef());
            }
        }

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
        return new SAML2Credentials(nameId, issuerEntityId, attributes,
            subjectAssertion.getConditions(), sessionIndex, authnContexts);
    }

    /**
     * Searches the sessionIndex in the assertion
     *
     * @param subjectAssertion assertion from the response
     * @return the sessionIndex if found in the assertion
     */
    protected String getSessionIndex(final Assertion subjectAssertion) {
        List<AuthnStatement> authnStatements = subjectAssertion.getAuthnStatements();
        if (authnStatements != null && authnStatements.size() > 0) {
            AuthnStatement statement = authnStatements.get(0);
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
    protected final void validateSamlProtocolResponse(final Response response, final SAML2MessageContext context,
                                                      final SignatureTrustEngine engine) {

        validateSuccess(response.getStatus());

        validateSignatureIfItExists(response.getSignature(), context, engine);

        validateIssueInstant(response.getIssueInstant());

        AuthnRequest request = null;
        final SAMLMessageStorage messageStorage = context.getSAMLMessageStorage();
        if (messageStorage != null && response.getInResponseTo() != null) {
            final XMLObject xmlObject = messageStorage.retrieveMessage(response.getInResponseTo());
            if (xmlObject == null) {
                throw new SAMLInResponseToMismatchException("InResponseToField of the Response doesn't correspond to sent message "
                    + response.getInResponseTo());
            } else if (xmlObject instanceof AuthnRequest) {
                request = (AuthnRequest) xmlObject;
            } else {
                throw new SAMLInResponseToMismatchException("Sent request was of different type than the expected AuthnRequest "
                    + response.getInResponseTo());
            }
        }

        verifyEndpoint(context.getSAMLEndpointContext().getEndpoint(), response.getDestination());
        if (request != null) {
            verifyRequest(request, context);
        }

        validateIssuerIfItExists(response.getIssuer(), context);
    }

    protected void verifyRequest(final AuthnRequest request, final SAML2MessageContext context) {
        // Verify endpoint requested in the original request
        final AssertionConsumerService assertionConsumerService = (AssertionConsumerService) context.getSAMLEndpointContext()
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
    protected final void validateSamlSSOResponse(final Response response, final SAML2MessageContext context,
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
        final NameID nameIdentifier = (NameID) context.getSAMLSubjectNameIdentifierContext().getSubjectNameIdentifier();
        if ((nameIdentifier == null || nameIdentifier.getValue() == null) && context.getBaseID() == null
            && (subjectConfirmations == null || subjectConfirmations.isEmpty())) {
            throw new SAMLException(
                "Subject NameID, BaseID and EncryptedID cannot be all null at the same time if there are no Subject Confirmations.");
        }
    }

    /**
     * Decrypt encrypted assertions and add them to the assertions list of the response.
     *
     * @param response  the response
     * @param decrypter the decrypter
     */
    protected final void decryptEncryptedAssertions(final Response response, final Decrypter decrypter) {

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
    protected final void validateAssertion(final Assertion assertion, final SAML2MessageContext context,
                                           final SignatureTrustEngine engine, final Decrypter decrypter) {

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
     * Validate the given subject by finding a valid Bearer confirmation. If the subject is valid, put its nameID in the context.
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
    protected final void validateSubject(final Subject subject, final SAML2MessageContext context,
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
            if (SubjectConfirmation.METHOD_BEARER.equals(confirmation.getMethod()) &&
                isValidBearerSubjectConfirmationData(confirmation.getSubjectConfirmationData(), context)) {
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
                    logger.warn("Could not find any Subject NameID/BaseID/EncryptedID, neither directly in the Subject nor in any Subject "
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
    protected final boolean isValidBearerSubjectConfirmationData(final SubjectConfirmationData data,
                                                                 final SAML2MessageContext context) {
        if (data == null) {
            logger.debug("SubjectConfirmationData cannot be null for Bearer confirmation");
            return false;
        }

        // TODO Validate inResponseTo

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

        if (!replayCache.get().check(getClass().getName(), assertion.getID(),
                data.getNotOnOrAfter().getMillis() + acceptedSkew * 1000)) {
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
    protected final void validateAssertionConditions(final Conditions conditions, final SAML2MessageContext context) {

        if (conditions == null) {
            return;
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
     * @param spEntityId           the sp entity id
     */
    protected final void validateAudienceRestrictions(final List<AudienceRestriction> audienceRestrictions,
                                                      final String spEntityId) {

        if (audienceRestrictions == null || audienceRestrictions.isEmpty()) {
            throw new SAMLAssertionAudienceException("Audience restrictions cannot be null or empty");
        }

        final Set<String> audienceUris = new HashSet<>();
        for (final AudienceRestriction audienceRestriction : audienceRestrictions) {
            if (audienceRestriction.getAudiences() != null) {
                for (final Audience audience : audienceRestriction.getAudiences()) {
                    audienceUris.add(audience.getAudienceURI());
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
    protected final void validateAuthenticationStatements(final List<AuthnStatement> authnStatements,
                                                          final SAML2MessageContext context) {

        for (final AuthnStatement statement : authnStatements) {
            if (!isAuthnInstantValid(statement.getAuthnInstant())) {
                throw new SAMLAuthnInstantException("Authentication issue instant is too old or in the future");
            }
            if (statement.getSessionNotOnOrAfter() != null && statement.getSessionNotOnOrAfter().isBeforeNow()) {
                throw new SAMLAuthnSessionCriteriaException("Authentication session between IDP and subject has ended");
            }
            // TODO implement authnContext validation
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
    protected final void validateAssertionSignature(final Signature signature, final SAML2MessageContext context,
                                                    final SignatureTrustEngine engine) {

        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();

        if (signature != null) {
            final String entityId = peerContext.getEntityId();
            validateSignature(signature, entityId, engine);
        } else {
            if (wantsAssertionsSigned(context) && !peerContext.isAuthenticated()) {
                throw new SAMLSignatureRequiredException("Assertion or response must be signed");
            }
        }
    }

    @VisibleForTesting
    Boolean wantsAssertionsSigned(SAML2MessageContext context) {
        if (context == null) return wantsAssertionsSigned;
        SPSSODescriptor spDescriptor = context.getSPSSODescriptor();
        if (spDescriptor == null) return wantsAssertionsSigned;
        return spDescriptor.getWantAssertionsSigned();
    }

    private boolean isAuthnInstantValid(final DateTime authnInstant) {
        return isDateValid(authnInstant, this.maximumAuthenticationLifetime);
    }

    @Override
    public final void setMaximumAuthenticationLifetime(final int maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }
}
