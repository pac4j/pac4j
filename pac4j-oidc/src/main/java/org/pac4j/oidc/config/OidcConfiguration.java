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
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.logout.handler.DefaultLogoutHandler;
import org.pac4j.core.logout.handler.LogoutHandler;
import org.pac4j.core.util.generator.RandomValueGenerator;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.oidc.profile.creator.TokenValidator;
import org.pac4j.oidc.util.SessionStoreValueRetriever;
import org.pac4j.oidc.util.ValueRetriever;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
@ToString(exclude = {"secret", "providerMetadata"})
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

    /* The private key JWT client authentication method configuration */
    private PrivateKeyJWTClientAuthnMethodConfig privateKeyJWTClientAuthnMethodConfig;

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

    private boolean allowUnsignedIdTokens;

    /** If enabled, try to process the access token as a JWT and include its claims in the profile.
     * Only enable this if there is an agreement between the IdP and the client about the format of
     * the access token. If not, the authorization server and the resource server might decide to
     * change the token format at any time (for example, by switching from this profile to opaque
     * tokens); hence, any logic in the client relying on the ability to read the access token
     * content would break without recourse.
     */
    private boolean includeAccessTokenClaimsInProfile = false;

    private String SSLFactory;

    @Override
    protected void internalInit(final boolean forceReinit) {
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
            try {
                setResourceRetriever(SSLFactory == null ?
                    new DefaultResourceRetriever(getConnectTimeout(),getReadTimeout()) :
                    new DefaultResourceRetriever(getConnectTimeout(),getReadTimeout(), 0, false,
                        (SSLSocketFactory) Class.forName(SSLFactory).getDeclaredConstructor().newInstance()));
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException
                | IllegalAccessException | NoSuchMethodException e) {
                throw new TechnicalException("SSLFactory loaded fail, please check your configuration");
            }
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

    public OIDCProviderMetadata findProviderMetadata() {
        init();

        return this.providerMetadata;
    }

    public void defaultDiscoveryURI(final String discoveryURI) {
        if (this.discoveryURI == null) {
            this.discoveryURI = discoveryURI;
        }
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

    public void setClientAuthenticationMethodAsString(final String auth) {
        this.clientAuthenticationMethod = ClientAuthenticationMethod.parse(auth);
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
            val methods = getProviderMetadata().getCodeChallengeMethods();
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

    public void setPreferredJwsAlgorithmAsString(final String preferredJwsAlgorithm) {
        this.preferredJwsAlgorithm = JWSAlgorithm.parse(preferredJwsAlgorithm);
    }

    public void configureHttpRequest(HTTPRequest request) {
        request.setConnectTimeout(getConnectTimeout());
        request.setReadTimeout(getReadTimeout());
    }

    public ResourceRetriever findResourceRetriever() {
        init();

        return resourceRetriever;
    }

    public void setResponseType(final String responseType) {
        try {
            this.responseType = ResponseType.parse(responseType);
        } catch (ParseException e) {
            throw new TechnicalException("Unrecognised responseType: " + responseType, e);
        }
    }

    public String findLogoutUrl() {
        init();

        if(logoutUrl == null && getProviderMetadata().getEndSessionEndpointURI() != null) {
            return getProviderMetadata().getEndSessionEndpointURI().toString();
        }
        return logoutUrl;
    }

    public int getTokenExpirationAdvance() {
        return isExpireSessionWithToken() ? tokenExpirationAdvance : -1;
    }

    public void setStateGenerator(final ValueGenerator stateGenerator) {
        assertNotNull("stateGenerator", stateGenerator);
        this.stateGenerator = stateGenerator;
    }

    public void setCodeVerifierGenerator(ValueGenerator codeVerifierGenerator) {
        assertNotNull("codeVerifierGenerator", codeVerifierGenerator);
        this.codeVerifierGenerator = codeVerifierGenerator;
    }

    public void setValueRetriever(ValueRetriever valueRetriever) {
        assertNotNull("valueRetriever", valueRetriever);
        this.valueRetriever = valueRetriever;
    }

    public LogoutHandler findLogoutHandler() {
        init();

        return logoutHandler;
    }

    public TokenValidator findTokenValidator() {
        if (this.tokenValidator == null) {
            setTokenValidator(new TokenValidator(this));
        }
        return tokenValidator;
    }

    public String getResponseType() {
        return responseType.toString();
    }
}
