package org.pac4j.openid4vp.wallet;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Plays the wallet side of a presentation, on strings only: no HTTP, no phone, no certificate.
 *
 * <p>It is deliberately free of any transport, so that the same simulator can be driven in a unit test
 * through a mock context, or over real HTTP from an application willing to see the flow in a browser.</p>
 *
 * <p>It is a testing aid, not a wallet: it validates nothing of what the verifier sends, which is precisely
 * what makes it useful to test a verifier in isolation.</p>
 *
 * <p>Every step is logged at debug level, on the same switch as the verifier itself
 * ({@code org.pac4j.openid4vp}), so that a single setting shows both sides of the conversation in the
 * order they happen. The arrows are written from the wallet point of view.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Slf4j
public class WalletSimulator {

    /**
     * <p>Read the request URI out of a wallet invocation URL.</p>
     *
     * @param walletUrl the URL handed over by the verifier
     * @return the URL the request object must be fetched from
     */
    public String readRequestUri(final String walletUrl) {
        LOGGER.debug("Wallet simulator <- invoked with: {}", walletUrl);
        val requestUri = readParameter(walletUrl, REQUEST_URI);
        if (requestUri == null) {
            throw new OpenId4VpException("no " + REQUEST_URI + " in the wallet URL: " + walletUrl);
        }
        LOGGER.debug("Wallet simulator -> about to fetch the request object at: {}", requestUri);
        return requestUri;
    }

    /**
     * <p>Read a query parameter out of a URL: what an HTTP client does for free when it follows one, and
     * what a driver must do by hand when it does not.</p>
     *
     * @param url the URL
     * @param name the name of the parameter
     * @return the value, or null when the URL does not carry it
     */
    public String readParameter(final String url, final String name) {
        val query = url.indexOf('?');
        if (query < 0) {
            return null;
        }
        for (val parameter : url.substring(query + 1).split("&")) {
            val separator = parameter.indexOf('=');
            if (separator > 0 && name.equals(parameter.substring(0, separator))) {
                return URLDecoder.decode(parameter.substring(separator + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    /**
     * <p>Read a request object, the way a wallet would before asking the holder for a consent.</p>
     *
     * @param signedRequestObject the request object served by the verifier
     * @return what the wallet needs to answer
     */
    @SuppressWarnings("unchecked")
    public WalletRequest readRequestObject(final String signedRequestObject) {
        try {
            val claims = SignedJWT.parse(signedRequestObject).getJWTClaimsSet();
            val metadata = claims.getJSONObjectClaim(CLIENT_METADATA);

            ECKey encryptionKey = null;
            if (metadata != null && metadata.get(JWKS) != null) {
                val keys = (List<Map<String, Object>>) ((Map<String, Object>) metadata.get(JWKS)).get(KEYS);
                if (keys != null && !keys.isEmpty()) {
                    encryptionKey = ECKey.parse(keys.get(0));
                }
            }
            val request = new WalletRequest(claims.getStringClaim(CLIENT_ID), claims.getStringClaim(NONCE),
                claims.getStringClaim(RESPONSE_URI), encryptionKey, claims.getJSONObjectClaim(DCQL_QUERY));
            LOGGER.debug("Wallet simulator <- request object from the verifier {}, expiring at {}",
                request.getClientId(), claims.getExpirationTime());
            LOGGER.debug("Wallet simulator    it asks for {} and expects the answer at {}",
                request.getDcqlQuery(), request.getResponseUri());
            LOGGER.debug("Wallet simulator    the answer must be bound to the nonce {} and encrypted to the key {}",
                request.getNonce(), encryptionKey != null ? encryptionKey.getKeyID() : "none");
            return request;
        } catch (final Exception e) {
            throw new OpenId4VpException("unable to read the request object", e);
        }
    }

    /**
     * <p>Build the response posted back to the verifier: the presentations, encrypted to the key the request
     * object published.</p>
     *
     * @param request the request being answered
     * @param vpToken the presentations, indexed by the identifier of the DCQL credential query they answer
     * @return the value of the response parameter
     */
    public String buildResponse(final WalletRequest request, final Map<String, List<String>> vpToken) {
        CommonHelper.assertNotNull("encryptionKey", request.getEncryptionKey());
        try {
            val claims = new JWTClaimsSet.Builder().claim(VP_TOKEN, vpToken).build();
            val response = new EncryptedJWT(
                new JWEHeader.Builder(JWEAlgorithm.ECDH_ES, EncryptionMethod.A128GCM).build(), claims);
            response.encrypt(new ECDHEncrypter(request.getEncryptionKey()));
            val serialized = response.serialize();
            LOGGER.debug("Wallet simulator -> posting {} presentation(s) to {}",
                vpToken.values().stream().mapToInt(List::size).sum(), request.getResponseUri());
            LOGGER.debug("Wallet simulator    answering the queries {}, encrypted with ECDH-ES / A128GCM ({} bytes)",
                vpToken.keySet(), serialized.length());
            return serialized;
        } catch (final Exception e) {
            throw new OpenId4VpException("unable to build the wallet response", e);
        }
    }
}
