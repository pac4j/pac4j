package org.pac4j.oauth.config;

import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.oauth.OAuthService;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.client.config.BaseClientConfiguration;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

/**
 * The base OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
@Setter
@ToString(exclude = "secret")
@Accessors(chain = true)
public abstract class OAuthConfiguration extends BaseClientConfiguration {

    /** Constant <code>OAUTH_TOKEN="oauth_token"</code> */
    public static final String OAUTH_TOKEN = "oauth_token";

    /** Constant <code>RESPONSE_TYPE_CODE="code"</code> */
    public static final String RESPONSE_TYPE_CODE = "code";

    protected String key;

    protected String secret;

    protected boolean tokenAsHeader;

    protected String responseType = RESPONSE_TYPE_CODE;

    protected String scope;

    protected HasBeenCancelledFactory hasBeenCancelledFactory = ctx -> false;

    protected OAuthProfileDefinition profileDefinition;

    protected HttpClientConfig httpClientConfig;

    protected Object api;

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("api", api);
        CommonHelper.assertNotBlank("key", this.key);
        CommonHelper.assertNotBlank("secret", this.secret);
        CommonHelper.assertNotNull("hasBeenCancelledFactory", hasBeenCancelledFactory);
        CommonHelper.assertNotNull("profileDefinition", profileDefinition);
    }

    /**
     * <p>buildService.</p>
     *
     * @param context a {@link WebContext} object
     * @param client a {@link IndirectClient} object
     * @return a {@link OAuthService} object
     */
    public abstract OAuthService buildService(final WebContext context, final IndirectClient client);
}
