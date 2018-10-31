package org.pac4j.saml.sso.impl;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.criterion.ProtocolCriterion;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.pac4j.saml.context.SAML2MessageContext;
import org.pac4j.saml.crypto.SAML2SignatureTrustEngineProvider;
import org.pac4j.saml.exceptions.SAMLIssuerException;
import org.pac4j.saml.exceptions.SAMLSignatureValidationException;
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

    protected AbstractSAML2ResponseValidator(final SAML2SignatureTrustEngineProvider signatureTrustEngineProvider) {
        this.signatureTrustEngineProvider = signatureTrustEngineProvider;
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

    protected boolean isDateValid(final DateTime issueInstant, final int interval) {
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

    protected boolean isIssueInstantValid(final DateTime issueInstant) {
        return isDateValid(issueInstant, 0);
    }

    @Override
    public final void setAcceptedSkew(final int acceptedSkew) {
        this.acceptedSkew = acceptedSkew;
    }
}
