package org.pac4j.oidc.config;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.core.util.generator.RandomValueGenerator;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.pac4j.oidc.util.SessionStoreValueRetriever;
import org.pac4j.oidc.util.ValueRetriever;
import org.pac4j.oidc.profile.creator.TokenValidator;

import static org.pac4j.core.util.CommonHelper.*;

/**
 * OpenID Connect configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcConfiguration extends BaseClientConfiguration {

    public static final String SCOPE = "scope";
    public static final String CUSTOM_PARAMS = "custom_params";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String RESPONSE_MODE = "response_mode";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String STATE = "state";
    public static final String MAX_AGE = "max_age";
    public static final String PROMPT = "prompt";
    public static final String NONCE = "nonce";
    public static final String CODE_CHALLENGE = "code_challenge";
    public static final String CODE_CHALLENGE_METHOD = "code_challenge_method";

    public static final List<ResponseType> AUTHORIZATION_CODE_FLOWS = Collections
        .unmodifiableList(Arrays.asList(new ResponseType(ResponseType.Value.CODE)));
    public static final List<ResponseType> IMPLICIT_FLOWS = Collections
        .unmodifiableList(Arrays.asList(new ResponseType(OIDCResponseTypeValue.ID_TOKEN),
            new ResponseType(OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN)));
    public static final List<ResponseType> HYBRID_CODE_FLOWS = Collections.unmodifiableList(Arrays.asList(
        new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN),
        new ResponseType(ResponseType.Value.CODE, ResponseType.Value.TOKEN),
        new ResponseType(ResponseType.Value.CODE, OIDCResponseTypeValue.ID_TOKEN, ResponseType.Value.TOKEN)));

    /* default max clock skew */
    public static final int DEFAULT_MAX_CLOCK_SKEW = 30;

    /* default time period advance (in seconds) for considering an access token expired */
    public static final int DEFAULT_TOKEN_EXPIRATION_ADVANCE = 0;

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

    /* use nonce? */
    private boolean useNonce;

    /* disable PKCE, even when supported by the IdP */
    private boolean disablePkce = false;

    /* use PKCE, when null, lookup support from metadata */
    private CodeChallengeMethod pkceMethod;

    /* Preferred JWS algorithm */
    private JWSAlgorithm preferredJwsAlgorithm;

    /* max_age seconds since the last time the End-User was actively authenticated by the OP */
    private Integer maxAge;

    /* max clock skew in seconds */
    private int maxClockSkew = DEFAULT_MAX_CLOCK_SKEW;

    private ResourceRetriever resourceRetriever;

    private OIDCProviderMetadata providerMetadata;

    private ResponseType responseType = AUTHORIZATION_CODE_FLOWS.get(0);

    private String responseMode;

    private String logoutUrl;

    private int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    private boolean withState = true;

    private Map<String, String> mappedClaims = new LinkedHashMap<>();

    private ValueGenerator stateGenerator = new RandomValueGenerator();

    private ValueGenerator codeVerifierGenerator = new RandomValueGenerator(50);

    private ValueRetriever valueRetriever = new SessionStoreValueRetriever();

    /* checks if sessions expire with token expiration (see also `tokenExpirationAdvance`) */
    private boolean expireSessionWithToken = true;

    /** time period advance (in seconds) for considering an access token expired */
    private int tokenExpirationAdvance = DEFAULT_TOKEN_EXPIRATION_ADVANCE;

    private LogoutHandler logoutHandler;

    private TokenValidator tokenValidator;

    @Override
    protected void internalInit() {
        // checks
        assertNotBlank("clientId", getClientId());
        if (!AUTHORIZATION_CODE_FLOWS.contains(responseType) && !IMPLICIT_FLOWS.contains(responseType)
            && !HYBRID_CODE_FLOWS.contains(responseType)) {
            throw new TechnicalException("Unsupported responseType: " + responseType);
        }
        // except for the implicit flow and when PKCE is disabled, the secret is mandatory
        if (!IMPLICIT_FLOWS.contains(responseType) && isDisablePkce()) {
            assertNotBlank("secret", getSecret());
        }
        if (this.getDiscoveryURI() == null && this.getProviderMetadata() == null) {
            throw new TechnicalException("You must define either the discovery URL or directly the provider metadata");
        }

        // default value
        if (getResourceRetriever() == null) {
            setResourceRetriever(new DefaultResourceRetriever(getConnectTimeout(),getReadTimeout()));
        }
        if (this.getProviderMetadata() == null) {
            assertNotBlank("discoveryURI", getDiscoveryURI());
            try {
                // Download OIDC metadata
                this.setProviderMetadata(OIDCProviderMetadata.parse(getResourceRetriever().retrieveResource(
                    new URL(this.getDiscoveryURI())).getContent()));
            } catch (final IOException | ParseException e) {
                throw new TechnicalException(e);
            }
        }
        if (this.logoutHandler == null) {
            this.logoutHandler = new DefaultLogoutHandler();
        }
    }

    public OIDCProviderMetadata getProviderMetadata() {
        return this.providerMetadata;
    }

    public OIDCProviderMetadata findProviderMetadata() {
        init();

        return this.providerMetadata;
    }

    public void setProviderMetadata(final OIDCProviderMetadata providerMetadata) {
        this.providerMetadata = providerMetadata;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(final String secret) {
        this.secret = secret;
    }

    public String getDiscoveryURI() {
        return discoveryURI;
    }

    public void defaultDiscoveryURI(final String discoveryURI) {
        if (this.discoveryURI == null) {
            this.discoveryURI = discoveryURI;
        }
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(final String scope) {
        this.scope = scope;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public String getCustomParam(String name) {
        return customParams.get(name);
    }

    public void setCustomParams(final Map<String, String> customParams) {
        assertNotNull("customParams", customParams);
        this.customParams = customParams;
    }

    public void addCustomParam(final String key, final String value) {
        this.customParams.put(key, value);
    }

    public ClientAuthenticationMethod getClientAuthenticationMethod() {
        return clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethod(final ClientAuthenticationMethod clientAuthenticationMethod) {
        this.clientAuthenticationMethod = clientAuthenticationMethod;
    }

    public void setClientAuthenticationMethodAsString(final String auth) {
        this.clientAuthenticationMethod = ClientAuthenticationMethod.parse(auth);
    }

    public boolean isUseNonce() {
        return useNonce;
    }

    public void setUseNonce(final boolean useNonce) {
        this.useNonce = useNonce;
    }

    public boolean isDisablePkce() {
        return disablePkce;
    }

    public void setDisablePkce(boolean disablePkce) {
        this.disablePkce = disablePkce;
    }

    public CodeChallengeMethod findPkceMethod() {
        init();

        if (isDisablePkce()) {
            return null;
        }
        if (getPkceMethod() == null) {
            if (getProviderMetadata() == null) {
                return null;
            }
            var methods = getProviderMetadata().getCodeChallengeMethods();
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

    public CodeChallengeMethod getPkceMethod() {
        return pkceMethod;
    }

    public void setPkceMethod(CodeChallengeMethod pkceMethod) {
        this.pkceMethod = pkceMethod;
    }

    public JWSAlgorithm getPreferredJwsAlgorithm() {
        return preferredJwsAlgorithm;
    }

    public void setPreferredJwsAlgorithm(final JWSAlgorithm preferredJwsAlgorithm) {
        this.preferredJwsAlgorithm = preferredJwsAlgorithm;
    }

    public void setPreferredJwsAlgorithmAsString(final String preferredJwsAlgorithm) {
        this.preferredJwsAlgorithm = JWSAlgorithm.parse(preferredJwsAlgorithm);
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(final Integer maxAge) {
        this.maxAge = maxAge;
    }

    public int getMaxClockSkew() {
        return maxClockSkew;
    }

    public void setMaxClockSkew(final int maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void configureHttpRequest(HTTPRequest request) {
        request.setConnectTimeout(getConnectTimeout());
        request.setReadTimeout(getReadTimeout());
    }

    public ResourceRetriever getResourceRetriever() {
        return resourceRetriever;
    }

    public ResourceRetriever findResourceRetriever() {
        init();

        return resourceRetriever;
    }

    public void setDiscoveryURI(final String discoveryURI) {
        this.discoveryURI = discoveryURI;
    }

    public void setResourceRetriever(final ResourceRetriever resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
    }

    public String getResponseType() {
        return responseType.toString();
    }

    public void setResponseType(final String responseType) {
        try {
            this.responseType = ResponseType.parse(responseType);
        } catch (ParseException e) {
            throw new TechnicalException("Unrecognised responseType: " + responseType, e);
        }
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(final String responseMode) {
        this.responseMode = responseMode;
    }

    public String findLogoutUrl() {
        init();

        if(logoutUrl == null && getProviderMetadata().getEndSessionEndpointURI() != null) {
            return getProviderMetadata().getEndSessionEndpointURI().toString();
        }
        return logoutUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(final String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public boolean isWithState() {
        return withState;
    }

    public void setWithState(final boolean withState) {
        this.withState = withState;
    }

    public boolean isExpireSessionWithToken() {
        return expireSessionWithToken;
    }

    public void setExpireSessionWithToken(boolean expireSessionWithToken) {
        this.expireSessionWithToken = expireSessionWithToken;
    }

    public int getTokenExpirationAdvance() {
        return isExpireSessionWithToken() ? tokenExpirationAdvance : -1;
    }

    public void setTokenExpirationAdvance(int tokenExpirationAdvance) {
        this.tokenExpirationAdvance = tokenExpirationAdvance;
    }

    public ValueGenerator getStateGenerator() {
        return stateGenerator;
    }

    public void setStateGenerator(final ValueGenerator stateGenerator) {
        assertNotNull("stateGenerator", stateGenerator);
        this.stateGenerator = stateGenerator;
    }

    public ValueGenerator getCodeVerifierGenerator() {
        return codeVerifierGenerator;
    }

    public void setCodeVerifierGenerator(ValueGenerator codeVerifierGenerator) {
        assertNotNull("codeVerifierGenerator", codeVerifierGenerator);
        this.codeVerifierGenerator = codeVerifierGenerator;
    }

    public ValueRetriever getValueRetriever() {
        return valueRetriever;
    }

    public void setValueRetriever(ValueRetriever valueRetriever) {
        assertNotNull("valueRetriever", valueRetriever);
        this.valueRetriever = valueRetriever;
    }

    public LogoutHandler findLogoutHandler() {
        init();

        return logoutHandler;
    }

    public void setLogoutHandler(final LogoutHandler logoutHandler) {
        this.logoutHandler = logoutHandler;
    }

    public TokenValidator getTokenValidator() {
        return tokenValidator;
    }

    public void setTokenValidator(final TokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    public TokenValidator findTokenValidator() {
        if (this.tokenValidator == null) {
            setTokenValidator(new TokenValidator(this));
        }
        return tokenValidator;
    }

    public Map<String, String> getMappedClaims() {
        return mappedClaims;
    }

    public void setMappedClaims(Map<String, String> mappedClaims) {
        this.mappedClaims = mappedClaims;
    }

    @Override
    public String toString() {
        return toNiceString(this.getClass(), "clientId", clientId, "secret", "[protected]",
            "discoveryURI", discoveryURI, "scope", scope, "customParams", customParams,
            "clientAuthenticationMethod", clientAuthenticationMethod, "useNonce", useNonce,
            "preferredJwsAlgorithm", preferredJwsAlgorithm, "maxAge", maxAge, "maxClockSkew", maxClockSkew,
            "connectTimeout", connectTimeout, "readTimeout", readTimeout, "resourceRetriever", resourceRetriever,
            "responseType", responseType, "responseMode", responseMode, "logoutUrl", logoutUrl,
            "withState", withState, "stateGenerator", stateGenerator, "logoutHandler", logoutHandler,
            "tokenValidator", tokenValidator, "mappedClaims", mappedClaims);
    }
}
