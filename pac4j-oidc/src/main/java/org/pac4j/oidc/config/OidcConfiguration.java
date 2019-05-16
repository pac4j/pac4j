package org.pac4j.oidc.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.*;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.core.state.StaticOrRandomStateGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * OpenID Connect configuration.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class OidcConfiguration extends InitializableObject {

    public static final String SCOPE = "scope";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String RESPONSE_MODE = "response_mode";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CLIENT_ID = "client_id";
    public static final String STATE = "state";
    public static final String MAX_AGE = "max_age";
    public static final String NONCE = "nonce";

    public static final List<String> AUTHORIZATION_CODE_FLOWS = Collections.unmodifiableList(Arrays.asList("code"));
    public static final List<String> IMPLICIT_FLOWS = Collections.unmodifiableList(Arrays.asList("id_token", "id_token token"));
    public static final List<String> HYBRID_CODE_FLOWS =
        Collections.unmodifiableList(Arrays.asList("code id_token", "code token", "code id_token token"));

    /* state attribute name in session */
    public static final String STATE_SESSION_ATTRIBUTE = "oidcStateAttribute";

    /* nonce attribute name in session */
    public static final String NONCE_SESSION_ATTRIBUTE = "oidcNonceAttribute";

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

    /* Preferred JWS algorithm */
    private JWSAlgorithm preferredJwsAlgorithm;

    /* max_age seconds since the last time the End-User was actively authenticated by the OP */
    private Integer maxAge;

    /* max clock skew in seconds */
    private int maxClockSkew = DEFAULT_MAX_CLOCK_SKEW;

    private ResourceRetriever resourceRetriever;

    private OIDCProviderMetadata providerMetadata;

    private String responseType = AUTHORIZATION_CODE_FLOWS.get(0);

    private String responseMode;

    private String logoutUrl;

    private int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    private boolean withState;

    private StateGenerator stateGenerator = new StaticOrRandomStateGenerator();

    /* checks if sessions expire with token expiration (see also `tokenExpirationAdvance`) */
    private boolean expireSessionWithToken = false;

    /** time period advance (in seconds) for considering an access token expired */
    private int tokenExpirationAdvance = DEFAULT_TOKEN_EXPIRATION_ADVANCE;

    @Override
    protected void internalInit() {
        // checks
        CommonHelper.assertNotBlank("clientId", getClientId());
        if (!AUTHORIZATION_CODE_FLOWS.contains(responseType) && !IMPLICIT_FLOWS.contains(responseType)
            && !HYBRID_CODE_FLOWS.contains(responseType)) {
            throw new TechnicalException("Unsupported responseType: " + responseType);
        }
        // except for the implicit flow, the secret is mandatory
        if (!IMPLICIT_FLOWS.contains(responseType)) {
            CommonHelper.assertNotBlank("secret", getSecret());
        }
        if (this.getDiscoveryURI() == null && this.getProviderMetadata() == null) {
            throw new TechnicalException("You must define either the discovery URL or directly the provider metadata");
        }

        // default value
        if (getResourceRetriever() == null) {
            setResourceRetriever(new DefaultResourceRetriever(getConnectTimeout(),getReadTimeout()));
        }
        if (this.getProviderMetadata() == null) {
            CommonHelper.assertNotBlank("discoveryURI", getDiscoveryURI());
            try {
                // Download OIDC metadata
                this.setProviderMetadata(OIDCProviderMetadata.parse(getResourceRetriever().retrieveResource(
                        new URL(this.getDiscoveryURI())).getContent()));
            } catch (final IOException | ParseException e) {
                throw new TechnicalException(e);
            }
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
        CommonHelper.assertNotNull("customParams", customParams);
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

    public JWSAlgorithm getPreferredJwsAlgorithm() {
        return preferredJwsAlgorithm;
    }

    public void setPreferredJwsAlgorithm(final JWSAlgorithm preferredJwsAlgorithm) {
        this.preferredJwsAlgorithm = preferredJwsAlgorithm;
    }

    public void setPreferredJwsAlgorithm(final String preferredJwsAlgorithm) {
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
        return responseType;
    }

    public void setResponseType(final String responseType) {
        this.responseType = responseType;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(final String responseMode) {
        this.responseMode = responseMode;
    }

    public String getLogoutUrl() {
        init();

        if(logoutUrl == null && getProviderMetadata().getEndSessionEndpointURI() != null) {
            return getProviderMetadata().getEndSessionEndpointURI().toString();
        }
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

    @Deprecated
    public String getStateData() {
        return ((StaticOrRandomStateGenerator) stateGenerator).getStateData();
    }

    @Deprecated
    public void setStateData(final String stateData) {
        ((StaticOrRandomStateGenerator) stateGenerator).setStateData(stateData);
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

    public StateGenerator getStateGenerator() {
        return stateGenerator;
    }

    public void setStateGenerator(final StateGenerator stateGenerator) {
        CommonHelper.assertNotNull("stateGenerator", stateGenerator);
        this.stateGenerator = stateGenerator;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "clientId", clientId, "secret", "[protected]",
            "discoveryURI", discoveryURI, "scope", scope, "customParams", customParams,
            "clientAuthenticationMethod", clientAuthenticationMethod, "useNonce", useNonce,
            "preferredJwsAlgorithm", preferredJwsAlgorithm, "maxAge", maxAge, "maxClockSkew", maxClockSkew,
            "connectTimeout", connectTimeout, "readTimeout", readTimeout, "resourceRetriever", resourceRetriever,
            "responseType", responseType, "responseMode", responseMode, "logoutUrl", logoutUrl,
            "withState", withState, "stateGenerator", stateGenerator);
    }
}
