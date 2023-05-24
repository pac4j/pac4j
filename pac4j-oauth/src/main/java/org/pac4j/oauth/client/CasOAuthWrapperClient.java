package org.pac4j.oauth.client;

import com.github.scribejava.core.model.Verb;
import lombok.Getter;
import lombok.Setter;
import org.pac4j.core.logout.CasLogoutActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfileDefinition;
import org.pac4j.scribe.builder.api.CasOAuthWrapperApi20;

/**
 * <p>This class is the OAuth client to authenticate users on CAS servers using OAuth wrapper.</p>
 * <p>The url of the OAuth endpoint of the CAS server must be set by using the {@link #setCasOAuthUrl(String)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfile}.</p>
 * <p>More information at https://wiki.jasig.org/display/CASUM/OAuth+server+support</p>
 *
 * @author Jerome Leleu
 * @since 1.3.0
 */
@Getter
@Setter
public class CasOAuthWrapperClient extends OAuth20Client {

    /**
     * The CAS OAuth server url (without a trailing slash).
     * For example: http://localhost:8080/cas/oauth2.0
     */
    private String casOAuthUrl;

    private String casLogoutUrl;

    // can be false for older CAS server versions
    private boolean isJsonTokenExtractor = true;

    // can be Verb.PUT for older CAS server versions
    private Verb accessTokenVerb = Verb.POST;

    private boolean implicitFlow = false;

    /**
     * <p>Constructor for CasOAuthWrapperClient.</p>
     */
    public CasOAuthWrapperClient() {
    }

    /**
     * <p>Constructor for CasOAuthWrapperClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     * @param casOAuthUrl a {@link String} object
     */
    public CasOAuthWrapperClient(final String key, final String secret, final String casOAuthUrl) {
        setKey(key);
        setSecret(secret);
        setCasOAuthUrl(casOAuthUrl);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("casOAuthUrl", this.casOAuthUrl);
        configuration.setApi(new CasOAuthWrapperApi20(this.casOAuthUrl, this.isJsonTokenExtractor, this.accessTokenVerb));
        configuration.setProfileDefinition(new CasOAuthWrapperProfileDefinition());
        if (this.implicitFlow) {
            configuration.setResponseType("token");
        } else {
            configuration.setResponseType("code");
        }
        setLogoutActionBuilderIfUndefined(new CasLogoutActionBuilder(casLogoutUrl, "service"));

        super.internalInit(forceReinit);
    }


    /**
     * <p>Setter for the field <code>casOAuthUrl</code>.</p>
     *
     * @param casOAuthUrl a {@link String} object
     */
    public void setCasOAuthUrl(final String casOAuthUrl) {
        CommonHelper.assertNotBlank("casOAuthUrl", casOAuthUrl);
        if (casOAuthUrl.endsWith("/")) {
            this.casOAuthUrl = casOAuthUrl.substring(0, casOAuthUrl.length() - 1);
        } else {
            this.casOAuthUrl = casOAuthUrl;
        }
    }
}
