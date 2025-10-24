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
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.exceptions.OidcException;
import org.pac4j.oidc.exceptions.OidcTokenException;

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
     * @param configuration a {@link OidcConfiguration} object
     * @param metadata a {@link OIDCProviderMetadata} object
     */
    public TokenValidator(final OidcConfiguration configuration, final OIDCProviderMetadata metadata) {
        CommonHelper.assertNotNull("configuration", configuration);
        CommonHelper.assertNotNull("metadata", metadata);
        this.configuration = configuration;
        this.metadata = metadata;

        // check algorithms
        val metadataAlgorithms = metadata.getIDTokenJWSAlgs();
        CommonHelper.assertTrue(CommonHelper.isNotEmpty(metadataAlgorithms),
            "There must at least one JWS algorithm supported on the OpenID Connect provider side");
        List<JWSAlgorithm> jwsAlgorithms = new ArrayList<>();
        val preferredAlgorithm = configuration.getPreferredJwsAlgorithm();
        if (metadataAlgorithms.contains(preferredAlgorithm)) {
            jwsAlgorithms.add(preferredAlgorithm);
        } else {
            jwsAlgorithms = metadataAlgorithms;
            if (preferredAlgorithm != null) {
                LOGGER.info("Preferred JWS algorithm: {} not available. Using all metadata algorithms: {}",
                    preferredAlgorithm, metadataAlgorithms);
            }
        }

        idTokenValidators = new ArrayList<>();
        logoutTokenValidators = new ArrayList<>();
        val _clientID = new ClientID(configuration.getClientId());

        for (var jwsAlgorithm : jwsAlgorithms) {
            // build validator
            final IDTokenValidator idTokenValidator;
            final LogoutTokenValidator logoutTokenValidator;
            if ("none".equals(jwsAlgorithm.getName())) {
                final String responseType = configuration.getResponseType();
                final boolean responseTypeContainsIdToken = responseType != null
                    && responseType.contains(OIDCResponseTypeValue.ID_TOKEN.toString());
                if (!configuration.isAllowUnsignedIdTokens() || responseTypeContainsIdToken) {
                    throw new OidcTokenException("Unsigned ID tokens are not allowed: " +
                        "they must be explicitly enabled on client side and " +
                        "the response_type used must return no ID Token from the authorization endpoint");
                }
                LOGGER.warn("Allowing unsigned ID tokens");
                idTokenValidator = new IDTokenValidator(metadata.getIssuer(), _clientID);
            } else if (StringUtils.isNotBlank(configuration.getSecret()) && (JWSAlgorithm.HS256.equals(jwsAlgorithm) ||
                JWSAlgorithm.HS384.equals(jwsAlgorithm) || JWSAlgorithm.HS512.equals(jwsAlgorithm))) {
                val _secret = new Secret(configuration.getSecret());
                idTokenValidator = createHMACTokenValidator(jwsAlgorithm, _clientID, _secret);
                if (metadata.supportsBackChannelLogout()) {
                    logoutTokenValidator = new LogoutTokenValidator(metadata.getIssuer(), _clientID, jwsAlgorithm, _secret);
                    logoutTokenValidator.setMaxClockSkew(configuration.getMaxClockSkew());
                    logoutTokenValidators.add(logoutTokenValidator);
                }
            } else {
                idTokenValidator = createRSAIdTokenValidator(jwsAlgorithm, _clientID);
                if (metadata.supportsBackChannelLogout()) {
                    logoutTokenValidator = createRSALogoutTokenValidator(jwsAlgorithm, _clientID);
                    logoutTokenValidator.setMaxClockSkew(configuration.getMaxClockSkew());
                    logoutTokenValidators.add(logoutTokenValidator);
                }
            }
            idTokenValidator.setMaxClockSkew(configuration.getMaxClockSkew());
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
                configuration.findResourceRetriever());
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
                configuration.findResourceRetriever());
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

        BadJOSEException badJOSEException = null;
        JOSEException joseException = null;
        for (val idTokenValidator : idTokenValidators) {
            LOGGER.debug("Trying IDToken validator: {}", idTokenValidator);
            try {
                return idTokenValidator.validate(idToken, expectedNonce);
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
