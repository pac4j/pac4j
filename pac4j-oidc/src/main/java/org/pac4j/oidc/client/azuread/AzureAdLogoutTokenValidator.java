package org.pac4j.oidc.client.azuread;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;

import java.text.ParseException;

public class AzureAdLogoutTokenValidator extends LogoutTokenValidator {

    private LogoutTokenValidator base;
    private String originalIssuer;

    /**
     * <p>Constructor for AzureAdLogoutTokenValidator.</p>
     *
     * @param base a {@link LogoutTokenValidator} object
     */
    public AzureAdLogoutTokenValidator(final LogoutTokenValidator base) {
        super(base.getExpectedIssuer(), base.getClientID(), base.getJWSKeySelector(), base.getJWEKeySelector());
        this.base = base;
        this.originalIssuer = base.getExpectedIssuer().getValue();
    }

    /** {@inheritDoc} */
    @Override
    public LogoutTokenClaimsSet validate(final JWT logoutToken) throws BadJOSEException, JOSEException {
        try {
            if (originalIssuer.contains("%7Btenantid%7D")) {
                var tid = logoutToken.getJWTClaimsSet().getClaim("tid");
                if (tid == null) {
                    throw new BadJWTException("ID token does not contain the 'tid' claim");
                }
                base = new LogoutTokenValidator(new Issuer(originalIssuer.replace("%7Btenantid%7D", tid.toString())),
                    base.getClientID(), base.getJWSKeySelector(), base.getJWEKeySelector());
                base.setMaxClockSkew(getMaxClockSkew());
            }
        } catch (ParseException e) {
            throw new BadJWTException(e.getMessage(), e);
        }
        return base.validate(logoutToken);
    }

}
