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
    
    private static final long serialVersionUID = 8379965156774379395L;
    
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
    
    public int getFollowingCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.FOLLOWING_COUNT));
    }
    
    /**
     * Indicate if the following_count attribute exists.
     * 
     * @return if the following_count attribute exists
     */
    public boolean isFollowingCountDefined() {
        return attributes.get(GitHubAttributesDefinition.FOLLOWING_COUNT) != null;
    }
    
    public String getBlog() {
        return (String) attributes.get(GitHubAttributesDefinition.BLOG);
    }
    
    public int getPublicRepoCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.PUBLIC_REPO_COUNT));
    }
    
    /**
     * Indicate if the public_repo_count attribute exists.
     * 
     * @return if the public_repo_count attribute exists
     */
    public boolean isPublicRepoCountDefined() {
        return attributes.get(GitHubAttributesDefinition.PUBLIC_REPO_COUNT) != null;
    }
    
    public int getPublicGistCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.PUBLIC_GIST_COUNT));
    }
    
    /**
     * Indicate if the public_gist_count attribute exists.
     * 
     * @return if the public_gist_count attribute exists
     */
    public boolean isPublicGistCountDefined() {
        return attributes.get(GitHubAttributesDefinition.PUBLIC_GIST_COUNT) != null;
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
    
    public int getOwnedPrivateRepoCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.OWNED_PRIVATE_REPO_COUNT));
    }
    
    /**
     * Indicate if the owned_private_repo_count attribute exists.
     * 
     * @return if the owned_private_repo_count attribute exists
     */
    public boolean isOwnedPrivateRepoCountDefined() {
        return attributes.get(GitHubAttributesDefinition.OWNED_PRIVATE_REPO_COUNT) != null;
    }
    
    public int getTotalPrivateRepoCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.TOTAL_PRIVATE_REPO_COUNT));
    }
    
    /**
     * Indicate if the total_private_repo_count attribute exists.
     * 
     * @return if the total_private_repo_count attribute exists
     */
    public boolean isTotalPrivateRepoCountDefined() {
        return attributes.get(GitHubAttributesDefinition.TOTAL_PRIVATE_REPO_COUNT) != null;
    }
    
    public int getPrivateGistCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.PRIVATE_GIST_COUNT));
    }
    
    /**
     * Indicate if the private_gist_count attribute exists.
     * 
     * @return if the private_gist_count attribute exists
     */
    public boolean isPrivateGistCountDefined() {
        return attributes.get(GitHubAttributesDefinition.PRIVATE_GIST_COUNT) != null;
    }
    
    public String getLogin() {
        return (String) attributes.get(GitHubAttributesDefinition.LOGIN);
    }
    
    public int getFollowersCount() {
        return getSafeInt((Integer) attributes.get(GitHubAttributesDefinition.FOLLOWERS_COUNT));
    }
    
    /**
     * Indicate if the followers_count attribute exists.
     * 
     * @return if the followers_count attribute exists
     */
    public boolean isFollowersCountDefined() {
        return attributes.get(GitHubAttributesDefinition.FOLLOWERS_COUNT) != null;
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
    
    public String getPermission() {
        return (String) attributes.get(GitHubAttributesDefinition.PERMISSION);
    }
    
    public String getGravatarId() {
        return (String) attributes.get(GitHubAttributesDefinition.GRAVATAR_ID);
    }
}
