package org.pac4j.openid4vp.wallet;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.crypto.ECDHEncrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.LinkedHashMap;
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
            LOGGER.debug("Wallet simulator <- request object from the verifier {}, expiring at {}",
                claims.getStringClaim(CLIENT_ID), claims.getExpirationTime());
            return buildRequest(claims.getClaims());
        } catch (final Exception e) {
            throw new OpenId4VpException("unable to read the request object", e);
        }
    }

    /**
     * <p>Whether the wallet URL points at a request object to fetch, or carries the request itself.</p>
     *
     * @param walletUrl the URL handed over by the verifier
     * @return whether a request object must be fetched
     */
    public boolean hasRequestUri(final String walletUrl) {
        return readParameter(walletUrl, REQUEST_URI) != null;
    }

    /**
     * <p>Read a request carried by the wallet URL itself, the way a wallet does when the verifier could not
     * sign: there is nothing to fetch, the parameters are the URL.</p>
     *
     * @param walletUrl the URL handed over by the verifier
     * @return what the wallet needs to answer
     */
    public WalletRequest readRequestParameters(final String walletUrl) {
        LOGGER.debug("Wallet simulator <- invoked with the request in the URL itself: {}", walletUrl);
        try {
            val parameters = new LinkedHashMap<String, Object>();
            for (val name : List.of(CLIENT_ID, NONCE, RESPONSE_URI, RESPONSE_MODE, RESPONSE_TYPE, STATE)) {
                val value = readParameter(walletUrl, name);
                if (value != null) {
                    parameters.put(name, value);
                }
            }
            // the query and the metadata are carried as JSON, since a URL parameter is only ever a string
            for (val name : List.of(DCQL_QUERY, CLIENT_METADATA)) {
                val value = readParameter(walletUrl, name);
                if (value != null) {
                    parameters.put(name, JSONObjectUtils.parse(value));
                }
            }
            return buildRequest(parameters);
        } catch (final Exception e) {
            throw new OpenId4VpException("unable to read the request from the wallet URL", e);
        }
    }

    /**
     * <p>What both forms of a request boil down to, once their parameters are in hand.</p>
     *
     * @param parameters the request parameters, whether claims of a request object or members of a URL
     * @return what the wallet needs to answer
     */
    @SuppressWarnings("unchecked")
    protected WalletRequest buildRequest(final Map<String, Object> parameters) throws ParseException {
        val metadata = (Map<String, Object>) parameters.get(CLIENT_METADATA);

        ECKey encryptionKey = null;
        if (metadata != null && metadata.get(JWKS) != null) {
            val keys = (List<Map<String, Object>>) ((Map<String, Object>) metadata.get(JWKS)).get(KEYS);
            if (keys != null && !keys.isEmpty()) {
                encryptionKey = ECKey.parse(keys.get(0));
            }
        }
        val request = new WalletRequest((String) parameters.get(CLIENT_ID), (String) parameters.get(NONCE),
            (String) parameters.get(RESPONSE_URI), encryptionKey, (Map<String, Object>) parameters.get(DCQL_QUERY));
        LOGGER.debug("Wallet simulator    it asks for {} and expects the answer at {}",
            request.getDcqlQuery(), request.getResponseUri());
        LOGGER.debug("Wallet simulator    the answer must be bound to the nonce {} and encrypted to the key {}",
            request.getNonce(), encryptionKey != null ? encryptionKey.getKeyID() : "none");
        return request;
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
