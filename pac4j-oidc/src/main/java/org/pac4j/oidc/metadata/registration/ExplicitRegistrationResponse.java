package org.pac4j.oidc.metadata.registration;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject.State;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsSet;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatementClaimsVerifier;
import com.nimbusds.openid.connect.sdk.federation.utils.JWTUtils;
import net.jcip.annotations.Immutable;

@Immutable
public final class ExplicitRegistrationResponse {
    public static final JOSEObjectType EXPLICIT_RESPONSE_JOSE_OBJECT_TYPE = new JOSEObjectType("explicit-registration-response+jwt");
    public static final ContentType CONTENT_TYPE;

    static {
        CONTENT_TYPE = new ContentType("application", EXPLICIT_RESPONSE_JOSE_OBJECT_TYPE.getType(), new ContentType.Parameter[0]);
    }

    private final SignedJWT statementJWT;
    private final EntityStatementClaimsSet claimsSet;

    private ExplicitRegistrationResponse(SignedJWT statementJWT, EntityStatementClaimsSet claimsSet) {
        if (statementJWT == null) {
            throw new IllegalArgumentException("The entity statement must not be null");
        } else if (State.UNSIGNED.equals(statementJWT.getState())) {
            throw new IllegalArgumentException("The statement is not signed");
        } else {
            this.statementJWT = statementJWT;
            if (claimsSet == null) {
                throw new IllegalArgumentException("The entity statement claims set must not be null");
            } else {
                this.claimsSet = claimsSet;
            }
        }
    }

    public static ExplicitRegistrationResponse sign(EntityStatementClaimsSet claimsSet, JWK signingJWK, JWSAlgorithm jwsAlg)
        throws JOSEException {
        if (claimsSet.isSelfStatement() && !claimsSet.getJWKSet().containsJWK(signingJWK)) {
            throw new JOSEException("Signing JWK not found in JWK set of self-statement");
        } else {
            try {
                return new ExplicitRegistrationResponse(
                    JWTUtils.sign(signingJWK, jwsAlg, EXPLICIT_RESPONSE_JOSE_OBJECT_TYPE, claimsSet.toJWTClaimsSet()), claimsSet);
            } catch (ParseException e) {
                throw new JOSEException(e.getMessage(), e);
            }
        }
    }

    public static ExplicitRegistrationResponse parse(SignedJWT signedStmt) throws ParseException {
        return new ExplicitRegistrationResponse(signedStmt, new EntityStatementClaimsSet(JWTUtils.parseSignedJWTClaimsSet(signedStmt)));
    }

    public static ExplicitRegistrationResponse parse(String signedStmtString) throws ParseException {
        try {
            return parse(SignedJWT.parse(signedStmtString));
        } catch (java.text.ParseException e) {
            throw new ParseException("Invalid entity statement: " + e.getMessage(), e);
        }
    }

    public EntityID getEntityID() {
        return this.getClaimsSet().getSubjectEntityID();
    }

    public SignedJWT getSignedStatement() {
        return this.statementJWT;
    }

    public EntityStatementClaimsSet getClaimsSet() {
        return this.claimsSet;
    }

    public Base64URL verifySignatureOfSelfStatement() throws BadJOSEException, JOSEException {
        if (!this.getClaimsSet().isSelfStatement()) {
            throw new BadJOSEException("Entity statement not self-issued");
        } else {
            return this.verifySignature(this.getClaimsSet().getJWKSet());
        }
    }

    public Base64URL verifySignature(JWKSet jwkSet) throws BadJOSEException, JOSEException {
        return JWTUtils.verifySignature(this.statementJWT, EXPLICIT_RESPONSE_JOSE_OBJECT_TYPE,
            new EntityStatementClaimsVerifier((Audience) null), jwkSet);
    }
}

