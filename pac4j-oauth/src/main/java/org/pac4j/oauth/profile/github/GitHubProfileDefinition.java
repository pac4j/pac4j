package org.pac4j.oauth.profile.github;

import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.Arrays;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the GitHub profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>TYPE="type"</code> */
    public static final String TYPE = "type";
    /** Constant <code>BLOG="blog"</code> */
    public static final String BLOG = "blog";
    /** Constant <code>URL="url"</code> */
    public static final String URL = "url";
    /** Constant <code>PUBLIC_GISTS="public_gists"</code> */
    public static final String PUBLIC_GISTS = "public_gists";
    /** Constant <code>FOLLOWING="following"</code> */
    public static final String FOLLOWING = "following";
    /** Constant <code>PRIVATE_GISTS="private_gists"</code> */
    public static final String PRIVATE_GISTS = "private_gists";
    /** Constant <code>PUBLIC_REPOS="public_repos"</code> */
    public static final String PUBLIC_REPOS = "public_repos";
    /** Constant <code>GRAVATAR_ID="gravatar_id"</code> */
    public static final String GRAVATAR_ID = "gravatar_id";
    /** Constant <code>AVATAR_URL="avatar_url"</code> */
    public static final String AVATAR_URL = "avatar_url";
    /** Constant <code>FOLLOWERS="followers"</code> */
    public static final String FOLLOWERS = "followers";
    /** Constant <code>LOGIN="login"</code> */
    public static final String LOGIN = "login";
    /** Constant <code>COMPANY="company"</code> */
    public static final String COMPANY = "company";
    /** Constant <code>HIREABLE="hireable"</code> */
    public static final String HIREABLE = "hireable";
    /** Constant <code>COLLABORATORS="collaborators"</code> */
    public static final String COLLABORATORS = "collaborators";
    /** Constant <code>HTML_URL="html_url"</code> */
    public static final String HTML_URL = "html_url";
    /** Constant <code>BIO="bio"</code> */
    public static final String BIO = "bio";
    /** Constant <code>TOTAL_PRIVATE_REPOS="total_private_repos"</code> */
    public static final String TOTAL_PRIVATE_REPOS = "total_private_repos";
    /** Constant <code>CREATED_AT="created_at"</code> */
    public static final String CREATED_AT = "created_at";
    /** Constant <code>UPDATED_AT="updated_at"</code> */
    public static final String UPDATED_AT = "updated_at";
    /** Constant <code>NAME="name"</code> */
    public static final String NAME = "name";
    /** Constant <code>DISK_USAGE="disk_usage"</code> */
    public static final String DISK_USAGE = "disk_usage";
    /** Constant <code>PLAN="plan"</code> */
    public static final String PLAN = "plan";
    /** Constant <code>OWNED_PRIVATE_REPOS="owned_private_repos"</code> */
    public static final String OWNED_PRIVATE_REPOS = "owned_private_repos";

    /**
     * <p>Constructor for GitHubProfileDefinition.</p>
     */
    public GitHubProfileDefinition() {
        super(x -> new GitHubProfile());
        Arrays.asList(new String[] {
            URL, COMPANY, NAME, BLOG, LOGIN, LOCATION, TYPE, GRAVATAR_ID, BIO
        }).forEach(a -> primary(a, Converters.STRING));
        Arrays.asList(new String[] {
            FOLLOWING, PUBLIC_REPOS, PUBLIC_GISTS, DISK_USAGE, COLLABORATORS, OWNED_PRIVATE_REPOS, TOTAL_PRIVATE_REPOS,
            PRIVATE_GISTS, FOLLOWERS
        }).forEach(a -> primary(a, Converters.INTEGER));
        primary(HIREABLE, Converters.BOOLEAN);
        primary(CREATED_AT, Converters.DATE_TZ_RFC822);
        primary(UPDATED_AT, Converters.DATE_TZ_RFC822);
        primary(AVATAR_URL, Converters.URL);
        primary(HTML_URL, Converters.URL);
        primary(PLAN, new JsonConverter(GitHubPlan.class));
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return "https://api.github.com/user";
    }

    /** {@inheritDoc} */
    @Override
    public GitHubProfile extractUserProfile(final String body) {
        val profile = (GitHubProfile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "id")));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
