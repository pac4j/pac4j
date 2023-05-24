package org.pac4j.oauth.profile.github;

import java.io.Serial;
import java.net.URI;
import java.util.Date;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for GitHub with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.GitHubClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfile extends OAuth20Profile {

    @Serial
    private static final long serialVersionUID = -8030906034414268058L;

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return (String) getAttribute(GitHubProfileDefinition.NAME);
    }

    /** {@inheritDoc} */
    @Override
    public String getUsername() {
        return (String) getAttribute(GitHubProfileDefinition.LOGIN);
    }

    /** {@inheritDoc} */
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(GitHubProfileDefinition.AVATAR_URL);
    }

    /** {@inheritDoc} */
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(GitHubProfileDefinition.HTML_URL);
    }

    /**
     * <p>getCompany.</p>
     *
     * @return a {@link String} object
     */
    public String getCompany() {
        return (String) getAttribute(GitHubProfileDefinition.COMPANY);
    }

    /**
     * <p>getFollowing.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFollowing() {
        return (Integer) getAttribute(GitHubProfileDefinition.FOLLOWING);
    }

    /**
     * <p>getBlog.</p>
     *
     * @return a {@link String} object
     */
    public String getBlog() {
        return (String) getAttribute(GitHubProfileDefinition.BLOG);
    }

    /**
     * <p>getPublicRepos.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getPublicRepos() {
        return (Integer) getAttribute(GitHubProfileDefinition.PUBLIC_REPOS);
    }

    /**
     * <p>getPublicGists.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getPublicGists() {
        return (Integer) getAttribute(GitHubProfileDefinition.PUBLIC_GISTS);
    }

    /**
     * <p>getDiskUsage.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getDiskUsage() {
        return (Integer) getAttribute(GitHubProfileDefinition.DISK_USAGE);
    }

    /**
     * <p>getCollaborators.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getCollaborators() {
        return (Integer) getAttribute(GitHubProfileDefinition.COLLABORATORS);
    }

    /**
     * <p>getPlan.</p>
     *
     * @return a {@link GitHubPlan} object
     */
    public GitHubPlan getPlan() {
        return (GitHubPlan) getAttribute(GitHubProfileDefinition.PLAN);
    }

    /**
     * <p>getOwnedPrivateRepos.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getOwnedPrivateRepos() {
        return (Integer) getAttribute(GitHubProfileDefinition.OWNED_PRIVATE_REPOS);
    }

    /**
     * <p>getTotalPrivateRepos.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getTotalPrivateRepos() {
        return (Integer) getAttribute(GitHubProfileDefinition.TOTAL_PRIVATE_REPOS);
    }

    /**
     * <p>getPrivateGists.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getPrivateGists() {
        return (Integer) getAttribute(GitHubProfileDefinition.PRIVATE_GISTS);
    }

    /**
     * <p>getFollowers.</p>
     *
     * @return a {@link Integer} object
     */
    public Integer getFollowers() {
        return (Integer) getAttribute(GitHubProfileDefinition.FOLLOWERS);
    }

    /**
     * <p>getCreatedAt.</p>
     *
     * @return a {@link Date} object
     */
    public Date getCreatedAt() {
        return (Date) getAttribute(GitHubProfileDefinition.CREATED_AT);
    }

    /**
     * <p>getUpdatedAt.</p>
     *
     * @return a {@link Date} object
     */
    public Date getUpdatedAt() {
        return (Date) getAttribute(GitHubProfileDefinition.UPDATED_AT);
    }

    /**
     * <p>getType.</p>
     *
     * @return a {@link String} object
     */
    public String getType() {
        return (String) getAttribute(GitHubProfileDefinition.TYPE);
    }

    /**
     * <p>getGravatarId.</p>
     *
     * @return a {@link String} object
     */
    public String getGravatarId() {
        return (String) getAttribute(GitHubProfileDefinition.GRAVATAR_ID);
    }

    /**
     * <p>getUrl.</p>
     *
     * @return a {@link String} object
     */
    public String getUrl() {
        return (String) getAttribute(GitHubProfileDefinition.URL);
    }

    /**
     * <p>getHireable.</p>
     *
     * @return a {@link Boolean} object
     */
    public Boolean getHireable() {
        return (Boolean) getAttribute(GitHubProfileDefinition.HIREABLE);
    }

    /**
     * <p>getBio.</p>
     *
     * @return a {@link String} object
     */
    public String getBio() {
        return (String) getAttribute(GitHubProfileDefinition.BIO);
    }
}
