package org.pac4j.oauth.client;

import com.github.scribejava.apis.LinkedInApi20;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Configuration;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile;
import org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileDefinition;

import java.util.Optional;

/**
 * <p>This class is the OAuth client to authenticate users in LinkedIn (using OAuth 2.0 protocol).</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.linkedin2.LinkedIn2Profile}.</p>
 * <p>The scope (by default : <code>r_fullprofile</code>) can be specified using the {@link #setScope(String)} method, as well as the
 * returned fields through the {@link #setFields(String)} method.</p>
 * <p>More information at https://developer.linkedin.com/documents/profile-api</p>
 *
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Client extends OAuth20Client<LinkedIn2Profile> {

    public LinkedIn2Client() {
        configuration = new LinkedIn2Configuration();
    }

    public LinkedIn2Client(final String key, final String secret) {
        configuration = new LinkedIn2Configuration();
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotBlank("scope", getConfiguration().getScope());
        CommonHelper.assertNotBlank("fields", getConfiguration().getFields());
        configuration.setApi(LinkedInApi20.instance());
        configuration.setProfileDefinition(new LinkedIn2ProfileDefinition());
        configuration.setWithState(true);
        configuration.setHasBeenCancelledFactory(ctx -> {
            final Optional<String> error = ctx.getRequestParameter(OAuthCredentialsException.ERROR);
            final Optional<String> errorDescription = ctx.getRequestParameter(OAuthCredentialsException.ERROR_DESCRIPTION);
            // user has denied permissions
            return error.map(e -> "access_denied".equals(e)).orElse(false)
                &&
                errorDescription.map(
                    e -> "the+user+denied+your+request".equals(e) || "the user denied your request".equals(e)
                ).orElse(false);
        });
        defaultLogoutActionBuilder((ctx, profile, targetUrl) ->
            Optional.of(RedirectAction.redirect("https://www.linkedin.com/uas/logout"))
        );

        super.clientInit();
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

    public String getFields() {
        return getConfiguration().getFields();
    }

    public void setFields(final String fields) {
        getConfiguration().setFields(fields);
    }
}
