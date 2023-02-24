package org.pac4j.oauth.client;

import com.github.scribejava.apis.GoogleApi20;
import lombok.val;
import org.pac4j.core.logout.GoogleLogoutActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.google2.Google2ProfileDefinition;

/**
 * <p>This class is the OAuth client to authenticate users in Google using OAuth protocol version 2.0.</p>
 * <p>The <i>scope</i> is by default : {@link org.pac4j.oauth.client.Google2Client.Google2Scope#EMAIL_AND_PROFILE},
 * but it can also but set to : {@link org.pac4j.oauth.client.Google2Client.Google2Scope#PROFILE} or
 * {@link org.pac4j.oauth.client.Google2Client.Google2Scope#EMAIL}.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.google2.Google2Profile}.</p>
 * <p>More information at https://developers.google.com/accounts/docs/OAuth2Login</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Client extends OAuth20Client {

    public enum Google2Scope {
        EMAIL,
        PROFILE,
        EMAIL_AND_PROFILE
    }

    /** Constant <code>PROFILE_SCOPE="profile"</code> */
    protected final static String PROFILE_SCOPE = "profile";

    /** Constant <code>EMAIL_SCOPE="email"</code> */
    protected final static String EMAIL_SCOPE = "email";

    protected Google2Scope scope = Google2Scope.EMAIL_AND_PROFILE;

    /**
     * <p>Constructor for Google2Client.</p>
     */
    public Google2Client() {
    }

    /**
     * <p>Constructor for Google2Client.</p>
     *
     * @param key a {@link java.lang.String} object
     * @param secret a {@link java.lang.String} object
     */
    public Google2Client(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotNull("scope", this.scope);
        final String scopeValue;
        if (this.scope == Google2Scope.EMAIL) {
            scopeValue = this.EMAIL_SCOPE;
        } else if (this.scope == Google2Scope.PROFILE) {
            scopeValue = this.PROFILE_SCOPE;
        } else {
            scopeValue = this.PROFILE_SCOPE + " " + this.EMAIL_SCOPE;
        }
        configuration.setApi(GoogleApi20.instance());
        configuration.setProfileDefinition(new Google2ProfileDefinition());
        configuration.setScope(scopeValue);
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            val error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            // user has denied autorization
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });
        setLogoutActionBuilderIfUndefined(new GoogleLogoutActionBuilder());

        super.internalInit(forceReinit);
    }

    /**
     * <p>Getter for the field <code>scope</code>.</p>
     *
     * @return a {@link org.pac4j.oauth.client.Google2Client.Google2Scope} object
     */
    public Google2Scope getScope() {
        return this.scope;
    }

    /**
     * <p>Setter for the field <code>scope</code>.</p>
     *
     * @param scope a {@link org.pac4j.oauth.client.Google2Client.Google2Scope} object
     */
    public void setScope(final Google2Scope scope) {
        this.scope = scope;
    }
}
