package org.pac4j.oauth.client;

import com.github.scribejava.apis.LinkedInApi20;
import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Configuration;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileCreator;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in LinkedIn (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link LinkedIn2Profile}.</p>
 * <p>The scope (by default : <code>r_fullprofile</code>) can be specified using the {@link #setScope(String)} method.</p>
 * <p>More information at https://developer.linkedin.com/documents/profile-api</p>
 *
 * @author Jerome Leleu
 * @author Vassilis Virvilis
 * @since 1.4.1
 */
public class LinkedIn2Client extends OAuth20Client {

    public LinkedIn2Client() {
        configuration = new LinkedIn2Configuration();
    }

    public LinkedIn2Client(final String key, final String secret) {
        this();
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("scope", getConfiguration().getScope());
        configuration.setApi(LinkedInApi20.instance());
        configuration.setProfileDefinition(new LinkedIn2ProfileDefinition());
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            val error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            val errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION).orElse(null);
            // user has denied authorizations
            if ("access_denied".equals(error)
                    && ("the+user+denied+your+request".equals(errorDescription) || "the user denied your request"
                    .equals(errorDescription))) {
                return true;
            }
            return false;
        });
        setLogoutActionBuilderIfUndefined((ctx, profile, targetUrl) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(ctx.webContext(), "https://www.linkedin.com/uas/logout")));
        setProfileCreatorIfUndefined(new LinkedIn2ProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }

    @Override
    public LinkedIn2Configuration getConfiguration() {
        return (LinkedIn2Configuration) configuration;
    }

    public String getScope() {
        return getConfiguration().getScope();
    }

    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }
}
