/*
  Copyright 2012 Jerome Leleu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.scribe.up.profile.github;

import java.util.Date;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for GitHub with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfile extends UserProfile {
    
    private static final long serialVersionUID = 3108552818323199961L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.githubDefinition;
    }
    
    public GitHubProfile() {
        super();
    }
    
    public GitHubProfile(Object id) {
        super(id);
    }
    
    public GitHubProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getCompany() {
        return (String) attributes.get(GitHubAttributesDefinition.COMPANY);
    }
    
    public String getName() {
        return (String) attributes.get(GitHubAttributesDefinition.NAME);
    }
    
    public int getFollowing() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.FOLLOWING));
    }
    
    /**
     * Indicate if the following attribute exists.
     * 
     * @return if the following attribute exists
     */
    public boolean isFollowingDefined() {
        return attributes.get(GitHubAttributesDefinition.FOLLOWING) != null;
    }
    
    public String getBlog() {
        return (String) attributes.get(GitHubAttributesDefinition.BLOG);
    }
    
    public int getPublicRepos() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.PUBLIC_REPOS));
    }
    
    /**
     * Indicate if the public_repos attribute exists.
     * 
     * @return if the public_repos attribute exists
     */
    public boolean isPublicReposDefined() {
        return attributes.get(GitHubAttributesDefinition.PUBLIC_REPOS) != null;
    }
    
    public int getPublicGists() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.PUBLIC_GISTS));
    }
    
    /**
     * Indicate if the public_gists attribute exists.
     * 
     * @return if the public_gists attribute exists
     */
    public boolean isPublicGistsDefined() {
        return attributes.get(GitHubAttributesDefinition.PUBLIC_GISTS) != null;
    }
    
    public int getDiskUsage() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.DISK_USAGE));
    }
    
    /**
     * Indicate if the disk_usage attribute exists.
     * 
     * @return if the disk_usage attribute exists
     */
    public boolean isDiskUsageDefined() {
        return attributes.get(GitHubAttributesDefinition.DISK_USAGE) != null;
    }
    
    public int getCollaborators() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.COLLABORATORS));
    }
    
    /**
     * Indicate if the collaborators attribute exists.
     * 
     * @return if the collaborators attribute exists
     */
    public boolean isCollaboratorsDefined() {
        return attributes.get(GitHubAttributesDefinition.COLLABORATORS) != null;
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) attributes.get(GitHubAttributesDefinition.PLAN);
    }
    
    public int getOwnedPrivateRepos() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.OWNED_PRIVATE_REPOS));
    }
    
    /**
     * Indicate if the owned_private_repos attribute exists.
     * 
     * @return if the owned_private_repos attribute exists
     */
    public boolean isOwnedPrivateReposDefined() {
        return attributes.get(GitHubAttributesDefinition.OWNED_PRIVATE_REPOS) != null;
    }
    
    public int getTotalPrivateRepos() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.TOTAL_PRIVATE_REPOS));
    }
    
    /**
     * Indicate if the total_private_repos attribute exists.
     * 
     * @return if the total_private_repos attribute exists
     */
    public boolean isTotalPrivateReposDefined() {
        return attributes.get(GitHubAttributesDefinition.TOTAL_PRIVATE_REPOS) != null;
    }
    
    public int getPrivateGists() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.PRIVATE_GISTS));
    }
    
    /**
     * Indicate if the private_gists attribute exists.
     * 
     * @return if the private_gists attribute exists
     */
    public boolean isPrivateGistsDefined() {
        return attributes.get(GitHubAttributesDefinition.PRIVATE_GISTS) != null;
    }
    
    public String getLogin() {
        return (String) attributes.get(GitHubAttributesDefinition.LOGIN);
    }
    
    public int getFollowers() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.FOLLOWERS));
    }
    
    /**
     * Indicate if the followers attribute exists.
     * 
     * @return if the followers attribute exists
     */
    public boolean isFollowersDefined() {
        return attributes.get(GitHubAttributesDefinition.FOLLOWERS) != null;
    }
    
    public Date getCreatedAt() {
        return (Date) attributes.get(GitHubAttributesDefinition.CREATED_AT);
    }
    
    public String getEmail() {
        return (String) attributes.get(GitHubAttributesDefinition.EMAIL);
    }
    
    public String getLocation() {
        return (String) attributes.get(GitHubAttributesDefinition.LOCATION);
    }
    
    public String getType() {
        return (String) attributes.get(GitHubAttributesDefinition.TYPE);
    }
    
    public String getGravatarId() {
        return (String) attributes.get(GitHubAttributesDefinition.GRAVATAR_ID);
    }
    
    public String getUrl() {
        return (String) attributes.get(GitHubAttributesDefinition.URL);
    }
    
    public String getAvatarUrl() {
        return (String) attributes.get(GitHubAttributesDefinition.AVATAR_URL);
    }
    
    public boolean isHireable() {
        return getSafeBoolean((Boolean) attributes.get(GitHubAttributesDefinition.HIREABLE));
    }
    
    /**
     * Indicate if the hireable attribute exists.
     * 
     * @return if the hireable attribute exists
     */
    public boolean isHireableDefined() {
        return attributes.get(GitHubAttributesDefinition.HIREABLE) != null;
    }
    
    public String getHtmlUrl() {
        return (String) attributes.get(GitHubAttributesDefinition.HTML_URL);
    }
    
    public String getBio() {
        return (String) attributes.get(GitHubAttributesDefinition.BIO);
    }
}
