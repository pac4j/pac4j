package org.pac4j.oauth.profile.github;

import java.util.Date;

import org.pac4j.core.profile.AttributesDefinition;
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

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new GitHubAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(GitHubAttributesDefinition.NAME);
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(GitHubAttributesDefinition.LOGIN);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(GitHubAttributesDefinition.AVATAR_URL);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(GitHubAttributesDefinition.HTML_URL);
    }
    
    public String getCompany() {
        return (String) getAttribute(GitHubAttributesDefinition.COMPANY);
    }
    
    public Integer getFollowing() {
        return (Integer) getAttribute(GitHubAttributesDefinition.FOLLOWING);
    }
    
    public String getBlog() {
        return (String) getAttribute(GitHubAttributesDefinition.BLOG);
    }
    
    public Integer getPublicRepos() {
        return (Integer) getAttribute(GitHubAttributesDefinition.PUBLIC_REPOS);
    }
    
    public Integer getPublicGists() {
        return (Integer) getAttribute(GitHubAttributesDefinition.PUBLIC_GISTS);
    }
    
    public Integer getDiskUsage() {
        return (Integer) getAttribute(GitHubAttributesDefinition.DISK_USAGE);
    }
    
    public Integer getCollaborators() {
        return (Integer) getAttribute(GitHubAttributesDefinition.COLLABORATORS);
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) getAttribute(GitHubAttributesDefinition.PLAN);
    }
    
    public Integer getOwnedPrivateRepos() {
        return (Integer) getAttribute(GitHubAttributesDefinition.OWNED_PRIVATE_REPOS);
    }
    
    public Integer getTotalPrivateRepos() {
        return (Integer) getAttribute(GitHubAttributesDefinition.TOTAL_PRIVATE_REPOS);
    }
    
    public Integer getPrivateGists() {
        return (Integer) getAttribute(GitHubAttributesDefinition.PRIVATE_GISTS);
    }
    
    public Integer getFollowers() {
        return (Integer) getAttribute(GitHubAttributesDefinition.FOLLOWERS);
    }
    
    public Date getCreatedAt() {
        return (Date) getAttribute(GitHubAttributesDefinition.CREATED_AT);
    }

    public Date getUpdatedAt() {
      return (Date) getAttribute(GitHubAttributesDefinition.UPDATED_AT);
    }

    public String getType() {
        return (String) getAttribute(GitHubAttributesDefinition.TYPE);
    }
    
    public String getGravatarId() {
        return (String) getAttribute(GitHubAttributesDefinition.GRAVATAR_ID);
    }
    
    public String getUrl() {
        return (String) getAttribute(GitHubAttributesDefinition.URL);
    }
    
    public Boolean getHireable() {
        return (Boolean) getAttribute(GitHubAttributesDefinition.HIREABLE);
    }
    
    public String getBio() {
        return (String) getAttribute(GitHubAttributesDefinition.BIO);
    }
}
