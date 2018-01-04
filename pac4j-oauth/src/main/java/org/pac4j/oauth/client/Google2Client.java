package org.pac4j.oauth.client;

import com.github.scribejava.apis.GoogleApi20;
import org.pac4j.core.logout.GoogleLogoutActionBuilder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.google2.Google2Profile;
import org.pac4j.oauth.profile.google2.Google2ProfileDefinition;

/**
 * <p>This class is the OAuth client to authenticate users in Google using OAuth protocol version 2.0.</p>
 * <p>The <i>scope</i> is by default : {@link Google2Scope#EMAIL_AND_PROFILE}, but it can also but set to : {@link Google2Scope#PROFILE}
 * or {@link Google2Scope#EMAIL}.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.google2.Google2Profile}.</p>
 * <p>More information at https://developers.google.com/accounts/docs/OAuth2Login</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2Client extends OAuth20Client<Google2Profile> {

    public enum Google2Scope {
        EMAIL,
        PROFILE,
        EMAIL_AND_PROFILE
    }

    protected final static String PROFILE_SCOPE = "profile";

    protected final static String EMAIL_SCOPE = "email";

    protected Google2Scope scope = Google2Scope.EMAIL_AND_PROFILE;

    public Google2Client() {
    }

    public Google2Client(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
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
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            // user has denied permissions
            if ("access_denied".equals(error)) {
                return true;
            }
            return false;
        });
        defaultLogoutActionBuilder(new GoogleLogoutActionBuilder<>());

        super.clientInit();
    }

    public Google2Scope getScope() {
        return this.scope;
    }

    public void setScope(final Google2Scope scope) {
        this.scope = scope;
    }
}
