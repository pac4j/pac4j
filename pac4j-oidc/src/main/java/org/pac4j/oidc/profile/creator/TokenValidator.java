package org.pac4j.oidc.profile.creator;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWT;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.claims.IDTokenClaimsSet;
import com.nimbusds.openid.connect.sdk.validators.IDTokenValidator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oidc.config.OidcConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ID Token validator.
 *
 * @author Jerome Leleu
 * @since 3.4.0
 */
public class TokenValidator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<IDTokenValidator> idTokenValidators;

    public TokenValidator(final OidcConfiguration configuration) {

        // check algorithms
        final List<JWSAlgorithm> metadataAlgorithms = configuration.findProviderMetadata().getIDTokenJWSAlgs();
        CommonHelper.assertTrue(CommonHelper.isNotEmpty(metadataAlgorithms),
            "There must at least one JWS algorithm supported on the OpenID Connect provider side");
        List<JWSAlgorithm> jwsAlgorithms = new ArrayList<>();
        final JWSAlgorithm preferredAlgorithm = configuration.getPreferredJwsAlgorithm();
        if (metadataAlgorithms.contains(preferredAlgorithm)) {
            jwsAlgorithms.add(preferredAlgorithm);
        } else {
            jwsAlgorithms = metadataAlgorithms;
            logger.warn("Preferred JWS algorithm: {} not available. Using all metadata algorithms: {}",
                preferredAlgorithm, metadataAlgorithms);
        }

        idTokenValidators = new ArrayList<>();
        final ClientID _clientID = new ClientID(configuration.getClientId());
        final Secret _secret = new Secret(configuration.getSecret());

        for (JWSAlgorithm jwsAlgorithm : jwsAlgorithms) {
            if ("none".equals(jwsAlgorithm.getName())) {
                jwsAlgorithm = null;
            }

            // build validator
            final IDTokenValidator idTokenValidator;
            if (jwsAlgorithm == null) {
                idTokenValidator = new IDTokenValidator(configuration.findProviderMetadata().getIssuer(), _clientID);
            } else if (CommonHelper.isNotBlank(configuration.getSecret()) && (JWSAlgorithm.HS256.equals(jwsAlgorithm) ||
                JWSAlgorithm.HS384.equals(jwsAlgorithm) || JWSAlgorithm.HS512.equals(jwsAlgorithm))) {
                idTokenValidator = createHMACTokenValidator(configuration, jwsAlgorithm, _clientID, _secret);
            } else {
                idTokenValidator = createRSATokenValidator(configuration, jwsAlgorithm, _clientID);
            }
            idTokenValidator.setMaxClockSkew(configuration.getMaxClockSkew());

            idTokenValidators.add(idTokenValidator);
        }
    }

    protected IDTokenValidator createRSATokenValidator(final OidcConfiguration configuration,
                                                       final JWSAlgorithm jwsAlgorithm, final ClientID clientID) {
        try {
            return new IDTokenValidator(configuration.findProviderMetadata().getIssuer(), clientID, jwsAlgorithm,
                configuration.findProviderMetadata().getJWKSetURI().toURL(), configuration.findResourceRetriever());
        } catch (final MalformedURLException e) {
            throw new TechnicalException(e);
        }
    }

    protected IDTokenValidator createHMACTokenValidator(final OidcConfiguration configuration, final JWSAlgorithm jwsAlgorithm,
                                                        final ClientID clientID, final Secret secret) {
        return new IDTokenValidator(configuration.findProviderMetadata().getIssuer(), clientID, jwsAlgorithm, secret);
    }

    public IDTokenClaimsSet validate(final JWT idToken, final Nonce expectedNonce)
        throws BadJOSEException, JOSEException {

        BadJOSEException badJOSEException = null;
        JOSEException joseException = null;
        for (final IDTokenValidator idTokenValidator : idTokenValidators) {

            try {
                return idTokenValidator.validate(idToken, expectedNonce);
            } catch (final BadJOSEException e1) {
                badJOSEException = e1;
            } catch (final JOSEException e2) {
                joseException = e2;
            }
        }

        if (badJOSEException != null) {
            throw badJOSEException;
        } else if (joseException != null) {
            throw joseException;
        } else {
            throw new TechnicalException("Unable to validate the ID token");
        }
    }
}
