package org.pac4j.oauth.client;

import com.github.scribejava.apis.FacebookApi;
import lombok.val;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.facebook.FacebookConfiguration;
import org.pac4j.oauth.profile.facebook.FacebookProfileCreator;
import org.pac4j.oauth.profile.facebook.FacebookProfileDefinition;

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
 * <code>FacebookConfiguration#setRequiresExtendedToken(boolean)</code> method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.facebook.FacebookProfile}.</p>
 * <p>More information at http://developers.facebook.com/docs/reference/api/user/</p>
 * <p>More information at https://developers.facebook.com/docs/howtos/login/extending-tokens/</p>
 *
 * @author Jerome Leleu
 * @author Mehdi BEN HAJ ABBES
 * @since 1.0.0
 */
public class FacebookClient extends OAuth20Client {

    /**
     * <p>Constructor for FacebookClient.</p>
     */
    public FacebookClient() {
        configuration = new FacebookConfiguration();
    }

    /**
     * <p>Constructor for FacebookClient.</p>
     *
     * @param key a {@link String} object
     * @param secret a {@link String} object
     */
    public FacebookClient(final String key, final String secret) {
        configuration = new FacebookConfiguration();
        setKey(key);
        setSecret(secret);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        CommonHelper.assertNotBlank("fields", getConfiguration().getFields());
        configuration.setApi(FacebookApi.instance());
        configuration.setProfileDefinition(new FacebookProfileDefinition());
        configuration.setHasBeenCancelledFactory(ctx -> {
            val error = ctx.getRequestParameter(OAuthCredentialsException.ERROR).orElse(null);
            val errorReason = ctx.getRequestParameter(OAuthCredentialsException.ERROR_REASON).orElse(null);
            val userDenied = "access_denied".equals(error) && "user_denied".equals(errorReason);
            val errorCode = ctx.getRequestParameter("error_code").orElse(null);
            val errorMessage = ctx.getRequestParameter("error_message").orElse(null);
            val hasError = CommonHelper.isNotBlank(errorCode) || CommonHelper.isNotBlank(errorMessage);
            if (userDenied || hasError) {
                return true;
            } else {
                return false;
            }
        });
        configuration.setWithState(true);
        setProfileCreatorIfUndefined(new FacebookProfileCreator(configuration, this));

        super.internalInit(forceReinit);
    }

    /** {@inheritDoc} */
    @Override
    public FacebookConfiguration getConfiguration() {
        return (FacebookConfiguration) configuration;
    }

    /**
     * <p>getScope.</p>
     *
     * @return a {@link String} object
     */
    public String getScope() {
        return getConfiguration().getScope();
    }

    /**
     * <p>setScope.</p>
     *
     * @param scope a {@link String} object
     */
    public void setScope(final String scope) {
        getConfiguration().setScope(scope);
    }

    /**
     * <p>getFields.</p>
     *
     * @return a {@link String} object
     */
    public String getFields() {
        return getConfiguration().getFields();
    }

    /**
     * <p>setFields.</p>
     *
     * @param fields a {@link String} object
     */
    public void setFields(final String fields) {
        getConfiguration().setFields(fields);
    }

    /**
     * <p>getLimit.</p>
     *
     * @return a int
     */
    public int getLimit() {
        return getConfiguration().getLimit();
    }

    /**
     * <p>setLimit.</p>
     *
     * @param limit a int
     */
    public void setLimit(final int limit) {
        getConfiguration().setLimit(limit);
    }
}
