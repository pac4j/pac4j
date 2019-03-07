package org.pac4j.oauth.client;

import com.github.scribejava.apis.FacebookApi;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.facebook.FacebookConfiguration;
import org.pac4j.oauth.profile.facebook.FacebookProfileCreator;
import org.pac4j.oauth.profile.facebook.FacebookProfileDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;

/**
 * <p>This class is the OAuth client to authenticate users in Facebook.</p>
 * <p>By default, the following <i>scope</i> is requested to Facebook: user_likes, user_about_me, user_birthday, user_education_history,
 * email, user_hometown, user_relationship_details, user_location, user_religion_politics, user_relationships, user_website and
 * user_work_history.</p>
 * <p>The <i>scope</i> can be defined to require permissions from the user and retrieve fields from Facebook, by using the
 * {@link #setScope(String)} method.</p>
 * <p>By default, the following <i>fields</i> are requested to Facebook: id, name, first_name, middle_name, last_name, gender, locale,
 * languages, link, third_party_id, timezone, updated_time, verified, about, birthday, education, email, hometown, interested_in,
 * location, political, favorite_athletes, favorite_teams, quotes, relationship_status, religion, significant_other, website and work.</p>
 * <p>The <i>fields</i> can be defined and requested to Facebook, by using the {@link #setFields(String)} method.</p>
 * <p>The number of results can be limited by using the {@link #setLimit(int)} method.</p>
 * <p>An extended access token can be requested by setting <code>true</code> on the
 * {@link FacebookConfiguration#setRequiresExtendedToken(boolean)} method.</p>
 * <p>It returns a {@link FacebookProfile}.</p>
 * <p>More information at http://developers.facebook.com/docs/reference/api/user/</p>
 * <p>More information at https://developers.facebook.com/docs/howtos/login/extending-tokens/</p>
 *
 * @author Jerome Leleu
 * @author Mehdi BEN HAJ ABBES
 * @since 1.0.0
 */
public class FacebookClient extends OAuth20Client {

    public FacebookClient() {
        configuration = new FacebookConfiguration();
    }

    public FacebookClient(final String key, final String secret) {
        configuration = new FacebookConfiguration();
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected void clientInit() {
        CommonHelper.assertNotBlank("fields", getConfiguration().getFields());
        configuration.setApi(FacebookApi.instance());
        configuration.setProfileDefinition(new FacebookProfileDefinition());
        configuration.setHasBeenCancelledFactory(ctx -> {
            final String error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            final String errorReason = ctx.getRequestParameter(OAuthCredentialsException.ERROR_REASON).orElse(null);
            // user has denied permissions
            if ("access_denied".equals(error) && "user_denied".equals(errorReason)) {
                return true;
            } else {
                return false;
            }
        });
        configuration.setWithState(true);
        defaultProfileCreator(new FacebookProfileCreator(configuration, this));

        super.clientInit();
    }

    @Override
    public FacebookConfiguration getConfiguration() {
        return (FacebookConfiguration) configuration;
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

    public int getLimit() {
        return getConfiguration().getLimit();
    }

    public void setLimit(final int limit) {
        getConfiguration().setLimit(limit);
    }
}
