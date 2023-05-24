package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth.OAuthService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.generator.RandomValueGenerator;
import org.pac4j.core.util.generator.ValueGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * The OAuh 2.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@ToString(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@With
@AllArgsConstructor
@NoArgsConstructor
public class OAuth20Configuration extends OAuthConfiguration {

    /** Constant <code>OAUTH_CODE="code"</code> */
    public static final String OAUTH_CODE = "code";

    /** Constant <code>STATE_REQUEST_PARAMETER="state"</code> */
    public static final String STATE_REQUEST_PARAMETER = "state";

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();

    private boolean withState;

    private ValueGenerator stateGenerator = new RandomValueGenerator();

    /**
     * {@inheritDoc}
     *
     * Build an OAuth service from the web context.
     */
    @Override
    public OAuthService buildService(final WebContext context, final IndirectClient client) {
        init();

        val finalCallbackUrl = client.computeFinalCallbackUrl(context);

        return ((DefaultApi20) api).createService(this.key, this.secret, finalCallbackUrl, this.scope,
            this.responseType, null, null, this.httpClientConfig, null);
    }

    /**
     * <p>Setter for the field <code>stateGenerator</code>.</p>
     *
     * @param stateGenerator a {@link ValueGenerator} object
     */
    public void setStateGenerator(final ValueGenerator stateGenerator) {
        CommonHelper.assertNotNull("stateGenerator", stateGenerator);
        this.stateGenerator = stateGenerator;
    }
}
