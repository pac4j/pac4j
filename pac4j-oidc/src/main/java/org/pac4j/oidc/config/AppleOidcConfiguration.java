package org.pac4j.oidc.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.store.GuavaStore;
import org.pac4j.core.store.Store;
import org.pac4j.core.util.CommonHelper;

import java.net.URI;
import java.security.interfaces.ECPrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Apple OpenID Connect configuration.
 *
 * @author Charley Wu
 * @since 5.0.0
 */
public class AppleOidcConfiguration extends OidcConfiguration {
    /**
     * Max expiration timeout for client secret (6 months in seconds)
     */
    private final static Duration MAX_TIMEOUT = Duration.ofSeconds(15777000);
    /**
     * Apple Auth Key
     */
    private ECPrivateKey privateKey;
    /**
     * Apple Auth Key ID
     */
    private String privateKeyID;
    /**
     * Apple Team ID
     */
    private String teamID;
    /**
     * Client secret cache store
     */
    private Store<String, String> store;
    /**
     * Client secret expiration timeout
     */
    private Duration timeout = MAX_TIMEOUT;

    @Override
    protected void internalInit() {
        // checks
        CommonHelper.assertNotBlank("privateKeyID", privateKeyID);
        CommonHelper.assertNotNull("privateKey", privateKey);
        CommonHelper.assertNotBlank("teamID", teamID);
        if (timeout.compareTo(MAX_TIMEOUT) > 0) {
            throw new IllegalArgumentException(String.format("timeout must not be greater then %d seconds", MAX_TIMEOUT.toSeconds()));
        }
        if (store == null) {
            store = new GuavaStore<>(1000, (int) timeout.toSeconds(), TimeUnit.SECONDS);
        }
        final OIDCProviderMetadata providerMetadata =
            new OIDCProviderMetadata(
                new Issuer("https://appleid.apple.com"),
                Collections.singletonList(SubjectType.PAIRWISE),
                // https://developer.apple.com/documentation/signinwithapplerestapi/fetch_apple_s_public_key_for_verifying_token_signature
                URI.create("https://appleid.apple.com/auth/keys"));
// https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_js/incorporating_sign_in_with_apple_into_other_platforms
        providerMetadata.setAuthorizationEndpointURI(URI.create("https://appleid.apple.com/auth/authorize"));
        // https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens
        providerMetadata.setTokenEndpointURI(URI.create("https://appleid.apple.com/auth/token"));
        providerMetadata.setIDTokenJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));
        setProviderMetadata(providerMetadata);
        setClientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
        super.internalInit();
    }

    /**
     * Generate client secret (JWT) and cache it until expiration timeout
     */
    @Override
    public String getSecret() {
        if (store != null) {
            Optional<String> cache = store.get(getClientId());
            if (cache.isPresent()) {
                return cache.get();
            }
        }
        // https://developer.apple.com/documentation/sign_in_with_apple/generate_and_validate_tokens#3262048
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .issuer(getTeamID())
            .audience("https://appleid.apple.com")
            .subject(getClientId())
            .issueTime(Date.from(Instant.now()))
            .expirationTime(Date.from(Instant.now().plusSeconds(timeout.toSeconds())))
            .build();
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(privateKeyID).build(),
            claimsSet);
        JWSSigner signer;
        try {
            signer = new ECDSASigner(privateKey);
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new TechnicalException(e);
        }
        String secret = signedJWT.serialize();
        if (store != null) {
            store.set(getClientId(), secret);
        }
        return secret;
    }

    public ECPrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(ECPrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getPrivateKeyID() {
        return privateKeyID;
    }

    public void setPrivateKeyID(String privateKeyID) {
        this.privateKeyID = privateKeyID;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public Store<String, String> getStore() {
        return store;
    }

    public void setStore(Store<String, String> store) {
        this.store = store;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }
}
