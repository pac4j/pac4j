package org.pac4j.saml.sso.impl;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLEndpointMismatchException;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.exceptions.SAMLInResponseToMismatchException;
import org.pac4j.saml.exceptions.SAMLIssueInstantException;
import org.pac4j.saml.exceptions.SAMLIssuerException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
import org.pac4j.saml.sso.SAML2ResponseValidator;
import org.pac4j.saml.storage.SAMLMessageStorage;
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
