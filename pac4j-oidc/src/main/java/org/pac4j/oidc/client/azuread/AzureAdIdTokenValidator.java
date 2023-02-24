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
    private IDTokenValidator base;
    private String originalIssuer;

    /**
     * <p>Constructor for AzureAdIdTokenValidator.</p>
     *
     * @param base a {@link com.nimbusds.openid.connect.sdk.validators.IDTokenValidator} object
     */
    public AzureAdIdTokenValidator(final IDTokenValidator base) {
        super(base.getExpectedIssuer(), base.getClientID());
        this.base = base;
        this.originalIssuer = base.getExpectedIssuer().getValue();
    }

    /** {@inheritDoc} */
    @Override
    public IDTokenClaimsSet validate(final JWT idToken, final Nonce expectedNonce) throws BadJOSEException, JOSEException {
        try {
            if (originalIssuer.contains("%7Btenantid%7D")) {
                var tid = idToken.getJWTClaimsSet().getClaim("tid");
                if (tid == null) {
                    throw new BadJWTException("ID token does not contain the 'tid' claim");
                }
                base = new IDTokenValidator(new Issuer(originalIssuer.replace("%7Btenantid%7D", tid.toString())),
                        base.getClientID(), base.getJWSKeySelector(), base.getJWEKeySelector());
                base.setMaxClockSkew(getMaxClockSkew());
            }
        } catch (ParseException e) {
            throw new BadJWTException(e.getMessage(), e);
        }
        return base.validate(idToken, expectedNonce);
    }
}
