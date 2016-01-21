/*
  Copyright 2012 - 2015 pac4j organization

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

    public AzureAdIdTokenValidator(final IDTokenValidator base) {
        super(base.getExpectedIssuer(), base.getClientID());
        this.base = base;
        this.originalIssuer = base.getExpectedIssuer().getValue();
    }

    @Override
    public IDTokenClaimsSet validate(final JWT idToken, final Nonce expectedNonce) throws BadJOSEException, JOSEException {
        try {
            if (originalIssuer.contains("%7Btenantid%7D")) {
                Object tid = idToken.getJWTClaimsSet().getClaim("tid");
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
