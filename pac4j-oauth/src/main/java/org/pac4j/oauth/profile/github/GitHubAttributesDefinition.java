package org.pac4j.oauth.profile.github;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the GitHub profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubAttributesDefinition extends AttributesDefinition {
    
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
    public static final String EMAIL = "email";
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
    public static final String LOCATION = "location";
    
    public GitHubAttributesDefinition() {
        Arrays.asList(new String[] {
            URL, COMPANY, NAME, BLOG, LOGIN, EMAIL, LOCATION, TYPE, GRAVATAR_ID, AVATAR_URL, HTML_URL, BIO
        }).forEach(a -> primary(a, Converters.STRING));
        Arrays.asList(new String[] {
            FOLLOWING, PUBLIC_REPOS, PUBLIC_GISTS, DISK_USAGE, COLLABORATORS, OWNED_PRIVATE_REPOS, TOTAL_PRIVATE_REPOS,
            PRIVATE_GISTS, FOLLOWERS
        }).forEach(a -> primary(a, Converters.INTEGER));
        primary(HIREABLE, Converters.BOOLEAN);
        primary(CREATED_AT, Converters.DATE_TZ_RFC822);
        primary(UPDATED_AT, Converters.DATE_TZ_RFC822);
        primary(PLAN, new JsonConverter<>(GitHubPlan.class));
    }
}
