/*
  Copyright 2012 -2014 Michael Remond

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.saml.sso;

import java.util.List;

import org.joda.time.DateTime;
import org.opensaml.common.SAMLObject;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.EncryptedAssertion;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.encryption.Decrypter;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.MetadataCriteria;
import org.opensaml.security.SAMLSignatureProfileValidator;
import org.opensaml.xml.encryption.DecryptionException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.SecurityException;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureTrustEngine;
import org.opensaml.xml.validation.ValidationException;
import org.pac4j.saml.context.ExtendedSAMLMessageContext;
import org.pac4j.saml.exceptions.SamlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class responsible for executing every required checks for validating a SAML response.
 * The method validateSamlResponse populates the given {@link ExtendedSAMLMessageContext}
 * with the correct SAML assertion and the corresponding nameID's Bearer subject if every checks succeeds.
 * 
 * @author Michael Remond
 * @since 1.5.0
 *
 */
public class Saml2ResponseValidator {

    private final static Logger logger = LoggerFactory.getLogger(Saml2ResponseValidator.class);

    /* maximum skew in seconds between SP and IDP clocks */
    private int acceptedSkew = 120;

    /* maximum lifetime after a successfull authentication on an IDP */
    private int maximumAuthenticationLifetime = 3600;

    /**
     * Validates the SAML protocol response and the SAML SSO response.
     * The method decrypt encrypted assertions if any.
     * 
     * @param context
     * @param engine
     * @param decrypter
     */
    public void validateSamlResponse(final ExtendedSAMLMessageContext context, final SignatureTrustEngine engine,
            final Decrypter decrypter) {

        SAMLObject message = context.getInboundSAMLMessage();

        if (!(message instanceof Response)) {
            throw new SamlException("Response instance is an unsupported type");
        }
        Response response = (Response) message;

        validateSamlProtocolResponse(response, context, engine);

        decryptEncryptedAssertions(response, decrypter);

        validateSamlSSOResponse(response, context, engine, decrypter);

    }

    /**
     * Validates the SAML protocol response:
     *  - IssueInstant
     *  - Issuer
     *  - StatusCode
     *  - Signature
     * 
     * @param response
     * @param context
     * @param engine
     */
    public void validateSamlProtocolResponse(final Response response, final ExtendedSAMLMessageContext context,
            final SignatureTrustEngine engine) {

        if (!isIssueInstantValid(response.getIssueInstant())) {
            throw new SamlException("Response issue instant is too old or in the future");
        }

        // TODO add Destination and inResponseTo Validation

        if (response.getIssuer() != null) {
            validateIssuer(response.getIssuer(), context);
        }

        if (!StatusCode.SUCCESS_URI.equals(response.getStatus().getStatusCode().getValue())) {
            String status = response.getStatus().getStatusCode().getValue();
            if (response.getStatus().getStatusMessage() != null) {
                status += " / " + response.getStatus().getStatusMessage().getMessage();
            }
            throw new SamlException("Authentication response is not success ; actual " + status);
        }

        if (response.getSignature() != null) {
            validateSignature(response.getSignature(), context.getPeerEntityId(), engine);
            context.setInboundSAMLMessageAuthenticated(true);
        }

    }

    /**
     * Validates the SAML SSO response by finding a valid assertion with authn statements.
     * Populates the {@link ExtendedSAMLMessageContext} with a subjectAssertion and a subjectNameIdentifier.
     * 
     * @param response
     * @param context
     * @param engine
     * @param decrypter
     */
    public void validateSamlSSOResponse(final Response response, final ExtendedSAMLMessageContext context,
            final SignatureTrustEngine engine, final Decrypter decrypter) {

        for (Assertion assertion : response.getAssertions()) {
            if (assertion.getAuthnStatements().size() > 0) {
                try {
                    validateAssertion(assertion, context, engine, decrypter);
                } catch (SamlException e) {
                    logger.error("Current assertion validation failed, continue with the next one", e);
                    continue;
                }
                context.setSubjectAssertion(assertion);
                break;
            }
        }

        if (context.getSubjectAssertion() == null) {
            throw new SamlException("No valid subject assertion found in response");
        }
        if (context.getSubjectNameIdentifier() == null) {
            throw new SamlException("Subject NameID cannot be null");
        }
    }

    /**
     * Decrypt encrypted assertions and add them to the assertions list of the response.
     * 
     * @param response
     * @param decrypter
     */
    protected void decryptEncryptedAssertions(final Response response, final Decrypter decrypter) {

        for (EncryptedAssertion encryptedAssertion : response.getEncryptedAssertions()) {
            try {
                Assertion decryptedAssertion = decrypter.decrypt(encryptedAssertion);
                response.getAssertions().add(decryptedAssertion);
            } catch (DecryptionException e) {
                logger.error("Decryption of assertion failed, continue with the next one", e);
            }
        }

    }

    /**
     * Validate issuer format and value.
     * 
     * @param issuer
     * @param context
     */
    protected void validateIssuer(final Issuer issuer, final ExtendedSAMLMessageContext context) {
        if (issuer.getFormat() != null && !issuer.getFormat().equals(NameIDType.ENTITY)) {
            throw new SamlException("Issuer type is not entity but " + issuer.getFormat());
        }
        if (!context.getPeerEntityMetadata().getEntityID().equals(issuer.getValue())) {
            throw new SamlException("Issuer " + issuer.getValue() + " does not match idp entityId "
                    + context.getPeerEntityMetadata().getEntityID());
        }
    }

    /**
     * Validate the given assertion:
     *  - issueInstant
     *  - issuer
     *  - subject
     *  - conditions
     *  - authnStatements
     *  - signature
     * 
     * @param assertion
     * @param context
     * @param engine
     * @param decrypter
     */
    protected void validateAssertion(final Assertion assertion, final ExtendedSAMLMessageContext context,
            final SignatureTrustEngine engine, final Decrypter decrypter) {

        if (!isIssueInstantValid(assertion.getIssueInstant())) {
            throw new SamlException("Assertion issue instant is too old or in the future");
        }

        validateIssuer(assertion.getIssuer(), context);

        if (assertion.getSubject() != null) {
            validateSubject(assertion.getSubject(), context, decrypter);
        } else {
            throw new SamlException("Assertion subject cannot be null");
        }

        validateAssertionConditions(assertion.getConditions(), context);

        validateAuthenticationStatements(assertion.getAuthnStatements(), context);

        validateAssertionSignature(assertion.getSignature(), context, engine);

    }

    /**
     * Validate the given subject by finding a valid Bearer confirmation. If the subject is valid,
     * put its nameID in the context.
     * 
     * @param subject
     * @param context
     * @param decrypter
     */
    @SuppressWarnings("unchecked")
    protected void validateSubject(final Subject subject, final ExtendedSAMLMessageContext context,
            final Decrypter decrypter) {

        for (SubjectConfirmation confirmation : subject.getSubjectConfirmations()) {
            if (SubjectConfirmation.METHOD_BEARER.equals(confirmation.getMethod())) {
                if (isValidBearerSubjectConfirmationData(confirmation.getSubjectConfirmationData(), context)) {
                    NameID nameID = null;
                    if (subject.getEncryptedID() != null) {
                        try {
                            nameID = (NameID) decrypter.decrypt(subject.getEncryptedID());
                        } catch (DecryptionException e) {
                            throw new SamlException("Decryption of nameID's subject failed", e);
                        }
                    } else {
                        nameID = subject.getNameID();
                    }
                    context.setSubjectNameIdentifier(nameID);
                    return;
                }
            }
        }

        throw new SamlException("Subject confirmation validation failed");
    }

    /**
     * Validate Bearer subject confirmation data
     *  - notBefore
     *  - NotOnOrAfter
     *  - recipient
     * 
     * @param data
     * @param context
     * @return true if all Bearer subject checks are passing
     */
    protected boolean isValidBearerSubjectConfirmationData(final SubjectConfirmationData data,
            final ExtendedSAMLMessageContext context) {
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

        if (data.getRecipient() == null) {
            logger.debug("SubjectConfirmationData recipient cannot be null for Bearer confirmation");
            return false;
        } else {
            if (!data.getRecipient().equals(context.getAssertionConsumerUrl())) {
                logger.debug("SubjectConfirmationData recipient {} does not match SP assertion consumer URL, found",
                        data.getRecipient());
                return false;
            }
        }
        return true;
    }

    /**
     * Validate assertionConditions
     *  - notBefore
     *  - notOnOrAfter
     * 
     * @param conditions
     * @param context
     */
    protected void validateAssertionConditions(final Conditions conditions, final ExtendedSAMLMessageContext context) {

        if (conditions == null) {
            throw new SamlException("Assertion conditions cannot be null");
        }

        if (conditions.getNotBefore() != null) {
            if (conditions.getNotBefore().minusSeconds(acceptedSkew).isAfterNow()) {
                throw new SamlException("Assertion condition notBefore is not valid");
            }
        }

        if (conditions.getNotOnOrAfter() != null) {
            if (conditions.getNotOnOrAfter().plusSeconds(acceptedSkew).isBeforeNow()) {
                throw new SamlException("Assertion condition notOnOrAfter is not valid");
            }
        }

        validateAudienceRestrictions(conditions.getAudienceRestrictions(), context.getLocalEntityId());

    }

    /**
     * Validate audience by matching the SP entityId.
     * 
     * @param audienceRestrictions
     * @param spEntityId
     */
    protected void validateAudienceRestrictions(final List<AudienceRestriction> audienceRestrictions,
            final String spEntityId) {

        if (audienceRestrictions == null || audienceRestrictions.size() == 0) {
            throw new SamlException("Audience restrictions cannot be null or empty");
        }
        if (!matchAudienceRestriction(audienceRestrictions, spEntityId)) {
            throw new SamlException("Assertion audience does not match SP configuration");
        }

    }

    /**
     * Validate the given authnStatements:
     *  - authnInstant
     *  - sessionNotOnOrAfter
     * 
     * @param authnStatements
     * @param context
     */
    protected void validateAuthenticationStatements(final List<AuthnStatement> authnStatements,
            final ExtendedSAMLMessageContext context) {

        for (AuthnStatement statement : authnStatements) {
            if (!isAuthnInstantValid(statement.getAuthnInstant())) {
                throw new SamlException("Authentication issue instant is too old or in the future");
            }
            if (statement.getSessionNotOnOrAfter() != null && statement.getSessionNotOnOrAfter().isBeforeNow()) {
                throw new SamlException("Authentication session between IDP and subject has ended");
            }
            // TODO implement authnContext validation
        }
    }

    /**
     * Validate assertion signature. If none is found and the SAML response did not have one and the SP requires
     * the assertions to be signed, the validation fails.
     * 
     * @param signature
     * @param context
     * @param engine
     */
    protected void validateAssertionSignature(final Signature signature, final ExtendedSAMLMessageContext context,
            final SignatureTrustEngine engine) {
        if (signature != null) {
            validateSignature(signature, context.getPeerEntityMetadata().getEntityID(), engine);
        } else if (((SPSSODescriptor) context.getLocalEntityRoleMetadata()).getWantAssertionsSigned()
                && !context.isInboundSAMLMessageAuthenticated()) {
            throw new SamlException("Assertion or response must be signed");
        }
    }

    /**
     * Validate the given digital signature by checking its profile and value.
     * 
     * @param signature
     * @param idpEntityId
     * @param trustEngine
     */
    protected void validateSignature(final Signature signature, final String idpEntityId,
            final SignatureTrustEngine trustEngine) {

        SAMLSignatureProfileValidator validator = new SAMLSignatureProfileValidator();
        try {
            validator.validate(signature);
        } catch (ValidationException e) {
            throw new SamlException("SAMLSignatureProfileValidator failed to validate signature", e);
        }

        CriteriaSet criteriaSet = new CriteriaSet();
        criteriaSet.add(new UsageCriteria(UsageType.SIGNING));
        criteriaSet.add(new MetadataCriteria(IDPSSODescriptor.DEFAULT_ELEMENT_NAME, SAMLConstants.SAML20P_NS));
        criteriaSet.add(new EntityIDCriteria(idpEntityId));

        boolean valid = false;
        try {
            valid = trustEngine.validate(signature, criteriaSet);
        } catch (SecurityException e) {
            throw new SamlException("An error occured during signature validation", e);
        }
        if (!valid) {
            throw new SamlException("Signature is not trusted");
        }
    }

    private boolean matchAudienceRestriction(final List<AudienceRestriction> audienceRestrictions,
            final String spEntityId) {
        for (AudienceRestriction audienceRestriction : audienceRestrictions) {
            if (audienceRestriction.getAudiences() != null) {
                for (Audience audience : audienceRestriction.getAudiences()) {
                    if (spEntityId.equals(audience.getAudienceURI())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isDateValid(final DateTime issueInstant, int interval) {
        long now = System.currentTimeMillis();
        return issueInstant.isBefore(now + acceptedSkew * 1000)
                && issueInstant.isAfter(now - (acceptedSkew + interval) * 1000);
    }

    private boolean isIssueInstantValid(final DateTime issueInstant) {
        return isDateValid(issueInstant, 0);
    }

    private boolean isAuthnInstantValid(DateTime authnInstant) {
        return isDateValid(authnInstant, maximumAuthenticationLifetime);
    }

    public void setAcceptedSkew(final int acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
    }

    public void setMaximumAuthenticationLifetime(int maximumAuthenticationLifetime) {
        this.maximumAuthenticationLifetime = maximumAuthenticationLifetime;
    }

}
