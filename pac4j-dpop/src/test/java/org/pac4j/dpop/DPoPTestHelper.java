package org.pac4j.dpop;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.dpop.DefaultDPoPProofFactory;
import com.nimbusds.oauth2.sdk.dpop.JWKThumbprintConfirmation;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.token.DPoPAccessToken;
import com.nimbusds.openid.connect.sdk.Nonce;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.JwkHelper;
import org.pac4j.dpop.util.DPoPConstants;
import org.pac4j.test.context.MockWebContext;

import java.net.URI;
import java.util.Date;

/**
 * Builds the access tokens, the proofs and the requests of the DPoP server tests.
 */
@UtilityClass
public class DPoPTestHelper {

    public static final String URL = "https://resource.example.org/protectedresource";

    private static final ECKey ISSUER_KEY = (ECKey) JwkHelper.generateKey(JWSAlgorithm.ES256);

    public JWK generateKey() {
        return JwkHelper.generateKey(JWSAlgorithm.ES256);
    }

    /**
     * Build a JWT access token.
     *
     * @param boundKey the key the access token is bound to, or null for an unbound one
     * @return the access token
     */
    public String accessToken(final JWK boundKey) throws Exception {
        val claims = new JWTClaimsSet.Builder().issuer("https://server.example.org").subject("user");
        if (boundKey != null) {
            val confirmation = new JWKThumbprintConfirmation(boundKey.computeThumbprint()).toJWTClaim();
            claims.claim(confirmation.getKey(), confirmation.getValue());
        }
        val jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.ES256), claims.build());
        jwt.sign(new ECDSASigner(ISSUER_KEY));
        return jwt.serialize();
    }

    public String proof(final JWK key, final String method, final String url, final String accessToken) throws Exception {
        return proof(key, method, url, accessToken, new Date(), null);
    }

    public String proof(final JWK key, final String method, final String url, final String accessToken, final Date creation,
                        final String nonce) throws Exception {
        return new DefaultDPoPProofFactory(key, JWSAlgorithm.parse(key.getAlgorithm().getName()))
            .createDPoPJWT(new JWTID(), method, URI.create(url), creation,
                accessToken == null ? null : new DPoPAccessToken(accessToken), nonce == null ? null : new Nonce(nonce))
            .serialize();
    }

    /**
     * Build a request to the protected resource.
     *
     * @param method the method
     * @param url the URL
     * @param accessToken the access token sent with the DPoP scheme (can be null)
     * @param proof the proof (can be null)
     * @return the web context
     */
    public MockWebContext request(final String method, final String url, final String accessToken, final String proof) {
        val context = MockWebContext.create().setRequestMethod(method).setFullRequestURL(url);
        if (accessToken != null) {
            context.addRequestHeader(HttpConstants.AUTHORIZATION_HEADER, HttpConstants.DPOP_HEADER_PREFIX + accessToken);
        }
        if (proof != null) {
            context.addRequestHeader(DPoPConstants.DPOP_HEADER, proof);
        }
        return context;
    }
}
