package org.pac4j.oidc.client.azuread;

import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;

/**
 * Specialized ID token validator cabable of handling the {tenantid} placeholder.
 *
 * @author Emond Papegaaij
 * @since 1.8.3
 */
public class AzureAdIdTokenValidator extends IDTokenValidator {
    private final IDTokenValidator base;
    private final String originalIssuer;

    /**
     * <p>Constructor for AzureAdIdTokenValidator.</p>
     *
     * @param base a {@link IDTokenValidator} object
     */
    public AzureAdIdTokenValidator(final IDTokenValidator base) {
        super(base.getExpectedIssuer(), base.getClientID());
        this.base = base;
        this.originalIssuer = base.getExpectedIssuer().getValue();
    }

    /** {@inheritDoc} */
    @Override
    public IDTokenClaimsSet validate(final JWT idToken, final Nonce expectedNonce) throws BadJOSEException, JOSEException {
        // this validator is shared by all requests: never store the tenant-specific validator in a field
        var validator = base;
        try {
            if (originalIssuer.contains("%7Btenantid%7D")) {
                var tid = idToken.getJWTClaimsSet().getClaim("tid");
                if (tid == null) {
                    throw new BadJWTException("ID token does not contain the 'tid' claim");
                }
                validator = new IDTokenValidator(new Issuer(originalIssuer.replace("%7Btenantid%7D", tid.toString())),
                        base.getClientID(), base.getJWSKeySelector(), base.getJWEKeySelector());
                validator.setMaxClockSkew(getMaxClockSkew());
            }
        } catch (ParseException e) {
            throw new BadJWTException(e.getMessage(), e);
        }
        return validator.validate(idToken, expectedNonce);
    }
}
