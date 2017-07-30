package org.pac4j.oauth.profile.github;

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
    
    private static final long serialVersionUID = -8030906034414268058L;

    @Override
    public String getDisplayName() {
        return (String) getAttribute(GitHubProfileDefinition.NAME);
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(GitHubProfileDefinition.LOGIN);
    }
    
    @Override
    public URI getPictureUrl() {
        return (URI) getAttribute(GitHubProfileDefinition.AVATAR_URL);
    }
    
    @Override
    public URI getProfileUrl() {
        return (URI) getAttribute(GitHubProfileDefinition.HTML_URL);
    }
    
    public String getCompany() {
        return (String) getAttribute(GitHubProfileDefinition.COMPANY);
    }
    
    public Integer getFollowing() {
        return (Integer) getAttribute(GitHubProfileDefinition.FOLLOWING);
    }
    
    public String getBlog() {
        return (String) getAttribute(GitHubProfileDefinition.BLOG);
    }
    
    public Integer getPublicRepos() {
        return (Integer) getAttribute(GitHubProfileDefinition.PUBLIC_REPOS);
    }
    
    public Integer getPublicGists() {
        return (Integer) getAttribute(GitHubProfileDefinition.PUBLIC_GISTS);
    }
    
    public Integer getDiskUsage() {
        return (Integer) getAttribute(GitHubProfileDefinition.DISK_USAGE);
    }
    
    public Integer getCollaborators() {
        return (Integer) getAttribute(GitHubProfileDefinition.COLLABORATORS);
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) getAttribute(GitHubProfileDefinition.PLAN);
    }
    
    public Integer getOwnedPrivateRepos() {
        return (Integer) getAttribute(GitHubProfileDefinition.OWNED_PRIVATE_REPOS);
    }
    
    public Integer getTotalPrivateRepos() {
        return (Integer) getAttribute(GitHubProfileDefinition.TOTAL_PRIVATE_REPOS);
    }
    
    public Integer getPrivateGists() {
        return (Integer) getAttribute(GitHubProfileDefinition.PRIVATE_GISTS);
    }
    
    public Integer getFollowers() {
        return (Integer) getAttribute(GitHubProfileDefinition.FOLLOWERS);
    }
    
    public Date getCreatedAt() {
        return (Date) getAttribute(GitHubProfileDefinition.CREATED_AT);
    }

    public Date getUpdatedAt() {
        return (Date) getAttribute(GitHubProfileDefinition.UPDATED_AT);
    }

    public String getType() {
        return (String) getAttribute(GitHubProfileDefinition.TYPE);
    }
    
    public String getGravatarId() {
        return (String) getAttribute(GitHubProfileDefinition.GRAVATAR_ID);
    }
    
    public String getUrl() {
        return (String) getAttribute(GitHubProfileDefinition.URL);
    }
    
    public Boolean getHireable() {
        return (Boolean) getAttribute(GitHubProfileDefinition.HIREABLE);
    }
    
    public String getBio() {
        return (String) getAttribute(GitHubProfileDefinition.BIO);
    }
}
