package org.pac4j.oauth.profile.github;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.Arrays;

/**
 * This class is the GitHub profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfileDefinition extends OAuth20ProfileDefinition<GitHubProfile, OAuth20Configuration> {

    public static final String TYPE = "type";
    public static final String BLOG = "blog";
    public static final String URL = "url";
    public static final String PUBLIC_GISTS = "public_gists";
    public static final String FOLLOWING = "following";
    public static final String PRIVATE_GISTS = "private_gists";
    public static final String PUBLIC_REPOS = "public_repos";
    public static final String GRAVATAR_ID = "gravatar_id";
    public static final String AVATAR_URL = "avatar_url";
    public static final String FOLLOWERS = "followers";
    public static final String LOGIN = "login";
    public static final String COMPANY = "company";
    public static final String HIREABLE = "hireable";
    public static final String COLLABORATORS = "collaborators";
    public static final String HTML_URL = "html_url";
    public static final String BIO = "bio";
    public static final String TOTAL_PRIVATE_REPOS = "total_private_repos";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String NAME = "name";
    public static final String DISK_USAGE = "disk_usage";
    public static final String PLAN = "plan";
    public static final String OWNED_PRIVATE_REPOS = "owned_private_repos";

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
        primary(PLAN, new JsonConverter<>(GitHubPlan.class));
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return "https://api.github.com/user";
    }

    @Override
    public GitHubProfile extractUserProfile(final String body) {
        final GitHubProfile profile = (GitHubProfile) newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, "id")));
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
