package org.pac4j.oidc.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.config.properties.JwksProperties;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.generator.RandomValueGenerator;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.method.ClientSecretJwtClientAuthnMethodConfig;
import org.pac4j.oidc.config.method.IPrivateKeyJwtClientAuthnMethodConfig;
import org.pac4j.oidc.exceptions.OidcConfigurationException;
import org.pac4j.oidc.federation.config.OidcFederationProperties;
import org.pac4j.oidc.metadata.IOidcOpMetadataResolver;
import org.pac4j.oidc.metadata.OidcFederationOpMetadataResolver;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;
import org.pac4j.oidc.util.SessionStoreValueRetriever;
import org.pac4j.oidc.util.ValueRetriever;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * OpenID Connect configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
@Getter
@Setter
@ToString(exclude = "secret")
@Accessors(chain = true)
@With
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class OidcConfiguration extends BaseClientConfiguration {
    // deprecate: move to OidcConstants
    /**
     * Constant <code>login_hint="user@example.org"</code>
     */
    public static final String LOGIN_HINT = "login_hint";
    /**
     * Constant <code>SCOPE="scope"</code>
     */
    public static final String SCOPE = "scope";
    /**
     * Constant <code>CUSTOM_PARAMS="custom_params"</code>
     */
    public static final String CUSTOM_PARAMS = "custom_params";
    /**
     * Constant <code>RESPONSE_TYPE="response_type"</code>
     */
    public static final String RESPONSE_TYPE = "response_type";
    /**
     * Constant <code>RESPONSE_MODE="response_mode"</code>
     */
    public static final String RESPONSE_MODE = "response_mode";
    /**
     * Constant <code>REDIRECT_URI="redirect_uri"</code>
     */
    public static final String REDIRECT_URI = "redirect_uri";
    /**
     * Constant <code>CLIENT_ID="client_id"</code>
     */
    public static final String CLIENT_ID = "client_id";
    /**
     * Constant <code>STATE="state"</code>
     */
    public static final String STATE = "state";
    /**
     * Constant <code>MAX_AGE="max_age"</code>
     */
    public static final String MAX_AGE = "max_age";
    /**
     * Constant <code>PROMPT="prompt"</code>
     */
    public static final String PROMPT = "prompt";
    /**
     * Constant <code>NONCE="nonce"</code>
     */
    public static final String NONCE = "nonce";
    /**
     * Constant <code>CODE_CHALLENGE="code_challenge"</code>
     */
    public static final String CODE_CHALLENGE = "code_challenge";
    /**
     * Constant <code>CODE_CHALLENGE_METHOD="code_challenge_method"</code>
     */
    public static final String CODE_CHALLENGE_METHOD = "code_challenge_method";

    /**
     * Constant <code>AUTHORIZATION_CODE_FLOWS</code>
     */
    public static final List<ResponseType> AUTHORIZATION_CODE_FLOWS = List.of(new ResponseType(ResponseType.Value.CODE));
    /**
     * Constant <code>IMPLICIT_FLOWS</code>
     */
    public static final List<ResponseType> IMPLICIT_FLOWS = Collections
        .unmodifiableList(Arrays.asList(new ResponseType(OIDCResponseTypeValue.ID_TOKEN),
            new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN)));
    /**
     * Constant <code>HYBRID_CODE_FLOWS</code>
     */
    public static final List<ResponseType> HYBRID_CODE_FLOWS = Collections.unmodifiableList(Arrays.asList(
        new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN),
        new ResponseType(ResponseType.Value.CODE, ResponseType.Value.TOKEN),
        new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN)));

    /* default max clock skew */
    /**
     * Constant <code>DEFAULT_MAX_CLOCK_SKEW=30</code>
     */
    public static final int DEFAULT_MAX_CLOCK_SKEW = 30;

    /* default time period advance (in seconds) for considering an access token expired */
    /**
     * Constant <code>DEFAULT_TOKEN_EXPIRATION_ADVANCE=0</code>
     */
    public static final int DEFAULT_TOKEN_EXPIRATION_ADVANCE = 0;

    private OidcFederationProperties federation = new OidcFederationProperties();

    private JwksProperties rpJwks = new JwksProperties();

    /* OpenID client identifier */
    private String clientId;

    /* OpenID secret */
    private String secret;

    /* discovery URI for fetching OP metadata (http://openid.net/specs/openid-connect-discovery-1_0.html) */
    private String discoveryURI;

    /* Scope */
    private String scope;

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();

    /* client authentication method used at token End Point */
    private ClientAuthenticationMethod clientAuthenticationMethod;

    /* Optional list of authentication methods supported by the client */
    private Set<ClientAuthenticationMethod> supportedClientAuthenticationMethods;

    /* The private key JWT client authentication method configuration */
    private IPrivateKeyJwtClientAuthnMethodConfig privateKeyJwtClientAuthnMethodConfig;

    /* The client secret JWT client authentication method configuration */
    private ClientSecretJwtClientAuthnMethodConfig clientSecretJwtClientAuthnMethodConfig;

    /* use nonce? */
    private boolean useNonce;

    /* use nonce on token obtained by refresh ? true by default to not change the initial behavior */
    private boolean useNonceOnRefresh = true;

    /* disable PKCE, even when supported by the OP */
    private boolean disablePkce = false;

    /* use PKCE, when null, lookup support from metadata */
    private CodeChallengeMethod pkceMethod;

    /* ID Token signing algorithm */
    private JWSAlgorithm idTokenSigningAlgorithm;

    /** Request Object signing algorithm */
    private JWSAlgorithm requestObjectSigningAlgorithm;

    /* max_age seconds since the last time the End-User was actively authenticated by the OP */
    private Integer maxAge;

    /* max clock skew in seconds */
    private int maxClockSkew = DEFAULT_MAX_CLOCK_SKEW;

    private ResourceRetriever resourceRetriever;

    private ResponseType responseType = AUTHORIZATION_CODE_FLOWS.get(0);

    private String responseMode;

    private String logoutUrl;

    private String loginHint;

    private int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    private boolean withState = true;

    private Map<String, String> mappedClaims = new LinkedHashMap<>();

    private ValueGenerator stateGenerator = new RandomValueGenerator();

    private ValueGenerator codeVerifierGenerator = new RandomValueGenerator(50);

    private ValueRetriever valueRetriever = new SessionStoreValueRetriever();

    /* checks if sessions expire with token expiration (see also `tokenExpirationAdvance`) */
    private boolean expireSessionWithToken = true;

    /**
     * time period advance (in seconds) for considering an access token expired
     */
    private int tokenExpirationAdvance = DEFAULT_TOKEN_EXPIRATION_ADVANCE;

    private boolean allowUnsignedIdTokens;

    /**
     * If enabled, try to process the access token as a JWT and include its claims in the profile.
     * Only enable this if there is an agreement between the IdP and the client about the format of
     * the access token. If not, the authorization server and the resource server might decide to
     * change the token format at any time (for example, by switching from this profile to opaque
     * tokens); hence, any logic in the client relying on the ability to read the access token
     * content would break without recourse.
     */
    private boolean includeAccessTokenClaimsInProfile = false;

    private SSLSocketFactory sslSocketFactory;

    private boolean callUserInfoEndpoint = true;

    private HostnameVerifier hostnameVerifier;

    protected IOidcOpMetadataResolver opMetadataResolver;

    @Deprecated(forRemoval = true)
    private boolean logoutValidation = true;

    private boolean pushedAuthorizationRequest;

    private OidcClient oidcClient;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void internalInit(final boolean forceReinit) {
        if (disablePkce) {
            LOGGER.info("PKCE is disabled for: " + oidcClient.getName());
        } else {
            LOGGER.info("PKCE is enabled for: " + oidcClient.getName());
        }
        // for federation, the clientId is not mandatory as it can be registered later on
        if (!isFederation()) {
            assertNotBlank("clientId", getClientId());
        }
        if (!AUTHORIZATION_CODE_FLOWS.contains(responseType) && !IMPLICIT_FLOWS.contains(responseType)
            && !HYBRID_CODE_FLOWS.contains(responseType)) {
            throw new OidcConfigurationException("Unsupported responseType: " + responseType);
        }
        // secret is mandatory if it's not the implicit flow and PKCE is disabled and not private_key_jwt
        // and not federation
        if (!IMPLICIT_FLOWS.contains(responseType) && isDisablePkce() && !isFederation()
            && clientAuthenticationMethod != ClientAuthenticationMethod.PRIVATE_KEY_JWT) {
            assertNotBlank("secret", getSecret());
        }
        if (this.getDiscoveryURI() == null && this.getOpMetadataResolver() == null && !isFederation()) {
            throw new OidcConfigurationException("You must define either the discovery URL or directly the provider metadata resolver "
                + "or the federation target entity (and the appropriate trust anchors)");
        }
        if (isFederation() && federation.isSendTrustChain() && !pushedAuthorizationRequest) {
            LOGGER.warn("The federation configuration requires to send the trust chain upfront without enabling the PAR: "
                + "this is a bad practice even if you have increased the header response size for the RP/OP");
        }

        if (forceReinit || getResourceRetriever() == null) {
            try {
                setResourceRetriever(new OidcResourceRetriever());
            } catch (final Exception e) {
                throw new OidcConfigurationException("SSLFactory loaded fail, please check your configuration");
            }
        }

        if (forceReinit || this.getOpMetadataResolver() == null) {
            this.opMetadataResolver = createNewOpMetadataResolver();
        }
    }

    public boolean isFederation() {
        return federation != null && StringUtils.isNotBlank(federation.getTargetOp());
    }

    /**
     * <p>Creates proper implementation of IOidcOpMetadataResolver.</p>
     */
    protected IOidcOpMetadataResolver createNewOpMetadataResolver() {
        if (StringUtils.isNotBlank(getDiscoveryURI())) {
            val resolver = new OidcOpMetadataResolver(this);
            resolver.init();
            return resolver;
        } else if (isFederation()) {
            val resolver = new OidcFederationOpMetadataResolver(this);
            resolver.init();
            return resolver;
        }
        throw new OidcConfigurationException("Unable to create IOidcOpMetadataResolver from configuration");
    }

    /**
     * <p>setDiscoveryURIIfUndefined.</p>
     *
     * @param discoveryURI a {@link String} object
     */
    public void setDiscoveryURIIfUndefined(final String discoveryURI) {
        if (this.discoveryURI == null) {
            this.discoveryURI = discoveryURI;
        }
    }

    /**
     * <p>getCustomParam.</p>
     *
     * @param name a {@link String} object
     * @return a {@link String} object
     */
    public String getCustomParam(String name) {
        return customParams.get(name);
    }

    /**
     * <p>Setter for the field <code>customParams</code>.</p>
     *
     * @param customParams a {@link Map} object
     */
    public void setCustomParams(final Map<String, String> customParams) {
        assertNotNull("customParams", customParams);
        this.customParams = customParams;
    }

    /**
     * <p>addCustomParam.</p>
     *
     * @param key   a {@link String} object
     * @param value a {@link String} object
     */
    public void addCustomParam(final String key, final String value) {
        this.customParams.put(key, value);
    }

    /**
     * <p>setClientAuthenticationMethodAsString.</p>
     *
     * @param auth a {@link String} object
     */
    public void setClientAuthenticationMethodAsString(final String auth) {
        this.clientAuthenticationMethod = ClientAuthenticationMethod.parse(auth);
    }

    /**
     * <p>findPkceMethod.</p>
     *
     * @return a {@link CodeChallengeMethod} object
     */
    public CodeChallengeMethod findPkceMethod() {
        init("this.findPkceMethod");

        if (isDisablePkce()) {
            return null;
        }
        if (getPkceMethod() == null) {
            ensuresMetadataResolverInitialized();
            val opMetadataResolver = getOpMetadataResolver().load();
            val methods = opMetadataResolver.getCodeChallengeMethods();
            if (methods == null || methods.isEmpty()) {
                return null;
            }
            if (methods.contains(CodeChallengeMethod.S256)) {
                return CodeChallengeMethod.S256;
            }
            return methods.get(0);
        }
        return getPkceMethod();
    }

    @Deprecated
    public void setPreferredJwsAlgorithmAsString(final String preferredJwsAlgorithm) {
        this.idTokenSigningAlgorithm = JWSAlgorithm.parse(preferredJwsAlgorithm);
    }

    public void setIdTokenSigningAlgorithmAsString(final String preferredJwsAlgorithm) {
        this.idTokenSigningAlgorithm = JWSAlgorithm.parse(preferredJwsAlgorithm);
    }

    /**
     * <p>configureHttpRequest.</p>
     *
     * @param request a {@link HTTPRequest} object
     */
    public void configureHttpRequest(HTTPRequest request) {
        request.setConnectTimeout(getConnectTimeout());
        request.setReadTimeout(getReadTimeout());
        request.setSSLSocketFactory(sslSocketFactory);
        request.setHostnameVerifier(hostnameVerifier);
    }

    /**
     * Use {@link #getResourceRetriever()}
     */
    @Deprecated
    public ResourceRetriever findResourceRetriever() {
        init("this.findResourceRetriever");

        return resourceRetriever;
    }

    /**
     * <p>Setter for the field <code>responseType</code>.</p>
     *
     * @param responseType a {@link String} object
     */
    public void setResponseType(final String responseType) {
        try {
            this.responseType = ResponseType.parse(responseType);
        } catch (ParseException e) {
            throw new OidcConfigurationException("Unrecognised responseType: " + responseType, e);
        }
    }

    /**
     * <p>findLogoutUrl.</p>
     *
     * @return a {@link String} object
     */
    public String findLogoutUrl() {
        init("this.findLogoutUrl");

        val opMetadataResolver = getOpMetadataResolver().load();
        if (logoutUrl == null && opMetadataResolver.getEndSessionEndpointURI() != null) {
            return opMetadataResolver.getEndSessionEndpointURI().toString();
        }
        return logoutUrl;
    }

    /**
     * <p>Getter for the field <code>tokenExpirationAdvance</code>.</p>
     *
     * @return a int
     */
    public int getTokenExpirationAdvance() {
        return isExpireSessionWithToken() ? tokenExpirationAdvance : -1;
    }

    /**
     * <p>Getter for the field <code>responseType</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getResponseType() {
        return responseType.toString();
    }

    private class OidcResourceRetriever extends DefaultResourceRetriever {

        public OidcResourceRetriever() {
            setConnectTimeout(OidcConfiguration.this.getConnectTimeout());
            setReadTimeout(OidcConfiguration.this.getReadTimeout());
            if (sslSocketFactory != null) {
                setSslSocketFactory(sslSocketFactory);
            }
        }

        @Override
        protected HttpURLConnection openHTTPConnection(URL url) throws IOException {
            var connection = super.openHTTPConnection(url);
            if (connection instanceof HttpsURLConnection) {
                if (sslSocketFactory != null) {
                    ((HttpsURLConnection) connection).setSSLSocketFactory(sslSocketFactory);
                }
                if (hostnameVerifier != null) {
                    ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
                }
            }
            return connection;
        }
    }

    /**
     * Use {@link #setPrivateKeyJwtClientAuthnMethodConfig(IPrivateKeyJwtClientAuthnMethodConfig)} instead.
     */
    @Deprecated
    public void setPrivateKeyJWTClientAuthnMethodConfig(final IPrivateKeyJwtClientAuthnMethodConfig clientAuthConfig) {
        this.privateKeyJwtClientAuthnMethodConfig = clientAuthConfig;
    }

    /**
     * Use {@link #getPrivateKeyJwtClientAuthnMethodConfig()} instead.
     */
    @Deprecated
    public IPrivateKeyJwtClientAuthnMethodConfig getPrivateKeyJWTClientAuthnMethodConfig() {
        return this.privateKeyJwtClientAuthnMethodConfig;
    }

    public IPrivateKeyJwtClientAuthnMethodConfig getPrivateKeyJwtClientAuthnMethodConfig() {
        return privateKeyJwtClientAuthnMethodConfig;
    }

    public void setPrivateKeyJwtClientAuthnMethodConfig(final IPrivateKeyJwtClientAuthnMethodConfig privateKeyJwtClientAuthnMethodConfig) {
        this.privateKeyJwtClientAuthnMethodConfig = privateKeyJwtClientAuthnMethodConfig;
    }

    /**
     * Use {@link #getIdTokenSigningAlgorithm()} instead.
     */
    @Deprecated
    public JWSAlgorithm getPreferredJwsAlgorithm() {
        return idTokenSigningAlgorithm;
    }

    /**
     * Use {@link #setIdTokenSigningAlgorithm(JWSAlgorithm)} instead.
     */
    @Deprecated
    public void setPreferredJwsAlgorithm(final JWSAlgorithm preferredJwsAlgorithm) {
        this.idTokenSigningAlgorithm = preferredJwsAlgorithm;
    }

    public JWSAlgorithm getIdTokenSigningAlgorithm() {
        return idTokenSigningAlgorithm;
    }

    public void setIdTokenSigningAlgorithm(final JWSAlgorithm idTokenJwsAlgorithm) {
        this.idTokenSigningAlgorithm = idTokenJwsAlgorithm;
    }

    public void ensuresMetadataResolverInitialized() {
        if (getOpMetadataResolver() == null) {
            reinit();
        }
    }

    public void setEnablePkce(final boolean enabled) {
        disablePkce = !enabled;
    }

    public boolean isEnablePkce() {
        return !disablePkce;
    }
}
