package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import com.nimbusds.openid.connect.sdk.validators.LogoutTokenValidator;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.OidcTokenException;
import org.pac4j.oidc.util.OidcHelper;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ID Token validator.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
@Slf4j
public class TokenValidator {

    private final List<IDTokenValidator> idTokenValidators;

    private final List<LogoutTokenValidator> logoutTokenValidators;

    private final OidcConfiguration configuration;

    private final OIDCProviderMetadata metadata;

    /**
     * <p>Constructor for TokenValidator.</p>
     *
     * @param config a {@link OidcConfiguration} object
     * @param metadata a {@link OIDCProviderMetadata} object
     */
    public TokenValidator(final OidcConfiguration config, final OIDCProviderMetadata metadata) {
        CommonHelper.assertNotNull("config", config);
        CommonHelper.assertNotNull("metadata", metadata);
        this.configuration = config;
        this.metadata = metadata;

        val idTokenJwsAlgs = OidcHelper.matchRPAlgAgainstOPAlgs("ID Token",
            config.getIdTokenSigningAlgorithm(), metadata.getIDTokenJWSAlgs());

        idTokenValidators = new ArrayList<>();
        logoutTokenValidators = new ArrayList<>();
        val _clientID = new ClientID(config.getClientId());

        for (var alg : idTokenJwsAlgs) {
            // build validator
            final IDTokenValidator idTokenValidator;
            final LogoutTokenValidator logoutTokenValidator;
            if ("none".equals(alg.getName())) {
                final String responseType = config.getResponseType();
                final boolean responseTypeContainsIdToken = responseType != null
                    && responseType.contains(OIDCResponseTypeValue.ID_TOKEN.toString());
                if (!config.isAllowUnsignedIdTokens() || responseTypeContainsIdToken) {
                    throw new OidcTokenException("Unsigned ID tokens are not allowed: " +
                        "they must be explicitly enabled on client side and " +
                        "the response_type used must return no ID Token from the authorization endpoint");
                }
                LOGGER.warn("Allowing unsigned ID tokens");
                idTokenValidator = new IDTokenValidator(metadata.getIssuer(), _clientID);
            } else if (StringUtils.isNotBlank(config.getSecret()) && (JWSAlgorithm.HS256.equals(alg) ||
                JWSAlgorithm.HS384.equals(alg) || JWSAlgorithm.HS512.equals(alg))) {
                val _secret = new Secret(config.getSecret());
                idTokenValidator = createHMACTokenValidator(alg, _clientID, _secret);
                if (metadata.supportsBackChannelLogout()) {
                    logoutTokenValidator = new LogoutTokenValidator(metadata.getIssuer(), _clientID, alg, _secret);
                    logoutTokenValidator.setMaxClockSkew(config.getMaxClockSkew());
                    logoutTokenValidators.add(logoutTokenValidator);
                }
            } else {
                idTokenValidator = createRSAIdTokenValidator(alg, _clientID);
                if (metadata.supportsBackChannelLogout()) {
                    logoutTokenValidator = createRSALogoutTokenValidator(alg, _clientID);
                    logoutTokenValidator.setMaxClockSkew(config.getMaxClockSkew());
                    logoutTokenValidators.add(logoutTokenValidator);
                }
            }
            idTokenValidator.setMaxClockSkew(config.getMaxClockSkew());
            idTokenValidators.add(idTokenValidator);
        }
    }

    /**
     * <p>createRSATokenValidator.</p>
     *
     * @param jwsAlgorithm a {@link JWSAlgorithm} object
     * @param clientID a {@link ClientID} object
     * @return a {@link IDTokenValidator} object
     */
    protected IDTokenValidator createRSAIdTokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        try {
            return new IDTokenValidator(metadata.getIssuer(), clientID, jwsAlgorithm, metadata.getJWKSetURI().toURL(),
                configuration.getResourceRetriever());
        } catch (final MalformedURLException e) {
            throw new OidcException(e);
        }
    }

    /**
     * <p>createRSATokenValidator.</p>
     *
     * @param jwsAlgorithm a {@link JWSAlgorithm} object
     * @param clientID a {@link ClientID} object
     * @return a {@link LogoutTokenValidator} object
     */
    protected LogoutTokenValidator createRSALogoutTokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        try {
            return new LogoutTokenValidator(metadata.getIssuer(), clientID, jwsAlgorithm, metadata.getJWKSetURI().toURL(),
                configuration.getResourceRetriever());
        } catch (final MalformedURLException e) {
            throw new OidcException(e);
        }
    }

    /**
     * <p>createHMACTokenValidator.</p>
     *
     * @param jwsAlgorithm a {@link JWSAlgorithm} object
     * @param clientID a {@link ClientID} object
     * @param secret a {@link Secret} object
     * @return a {@link IDTokenValidator} object
     */
    protected IDTokenValidator createHMACTokenValidator(final JWSAlgorithm jwsAlgorithm, final ClientID clientID, final Secret secret) {
        return new IDTokenValidator(metadata.getIssuer(), clientID, jwsAlgorithm, secret);
    }

    /**
     * <p>validate the ID token.</p>
     *
     * @param idToken a {@link JWT} object
     * @param expectedNonce a {@link Nonce} object
     * @return a {@link IDTokenClaimsSet} object
     * @throws BadJOSEException if any.
     * @throws JOSEException if any.
     */
    public IDTokenClaimsSet validateIdToken(final JWT idToken, final Nonce expectedNonce)
        throws BadJOSEException, JOSEException {

        String jws;
        String jwe;
        BadJOSEException badJOSEException = null;
        JOSEException joseException = null;
        for (val idTokenValidator : idTokenValidators) {
            jws = Pac4jConstants.EMPTY_STRING;
            val jwsSelector = idTokenValidator.getJWSKeySelector();
            if (jwsSelector != null) {
                jws = jwsSelector.getClass().getSimpleName();
            }
            jwe = Pac4jConstants.EMPTY_STRING;
            val jweSelector = idTokenValidator.getJWEKeySelector();
            if (jweSelector != null) {
                jwe = jweSelector.getClass().getSimpleName();
            }
            LOGGER.debug("Trying IDToken validator, issuer: {}, type: {}, JWS: {}, JWE: {}", idTokenValidator.getExpectedIssuer(),
                idTokenValidator.getExpectedJWTType(), jws, jwe);
            try {
                val validated = idTokenValidator.validate(idToken, expectedNonce);
                LOGGER.debug("Validated: {}", validated);
                return validated;
            } catch (final BadJOSEException e1) {
                LOGGER.debug(e1.getMessage(), e1);
                badJOSEException = e1;
            } catch (final JOSEException e2) {
                LOGGER.debug(e2.getMessage(), e2);
                joseException = e2;
            }
        }

        if (badJOSEException != null) {
            throw badJOSEException;
        } else if (joseException != null) {
            throw joseException;
        } else {
            throw new OidcTokenException("Unable to validate the ID token");
        }
    }

    /**
     * <p>validate the logout token.</p>
     *
     * @param logoutToken a {@link JWT} object
     * @return a {@link IDTokenClaimsSet} object
     * @throws BadJOSEException if any.
     * @throws JOSEException if any.
     */
    public LogoutTokenClaimsSet validateLogoutToken(final JWT logoutToken)
        throws BadJOSEException, JOSEException {

        BadJOSEException badJOSEException = null;
        JOSEException joseException = null;
        for (val logoutTokenValidator : logoutTokenValidators) {
            LOGGER.debug("Trying LogoutToken validator: {}", logoutToken);
            try {
                return logoutTokenValidator.validate(logoutToken);
            } catch (final BadJOSEException e1) {
                LOGGER.debug(e1.getMessage(), e1);
                badJOSEException = e1;
            } catch (final JOSEException e2) {
                LOGGER.debug(e2.getMessage(), e2);
                joseException = e2;
            }
        }

        if (badJOSEException != null) {
            throw badJOSEException;
        } else if (joseException != null) {
            throw joseException;
        } else {
            throw new OidcTokenException("Unable to validate the logout token");
        }
    }

    // for tests
    List<IDTokenValidator> getIdTokenValidators() {
        return idTokenValidators;
    }

    // for tests
    List<LogoutTokenValidator> getLogoutTokenValidators() {
        return logoutTokenValidators;
    }
}
