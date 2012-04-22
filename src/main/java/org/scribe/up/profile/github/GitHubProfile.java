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
import org.scribe.up.profile.ProfileDefinitions;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.impl.GitHubProvider;

/**
 * This class is the user profile for GitHub with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfile extends UserProfile {
    
    private static final long serialVersionUID = -7024887450473712689L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return ProfileDefinitions.githubDefinition;
    }
    
    protected String getProviderType() {
        return GitHubProvider.TYPE;
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
    
    /**
     * Indicate if the user identifier matches this kind of profile.
     * 
     * @param id
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(String id) {
        if (id != null && id.startsWith(GitHubProvider.TYPE + SEPARATOR)) {
            return true;
        } else {
            return false;
        }
    }
    
    public String getCompany() {
        return (String) attributes.get(GitHubProfileDefinition.COMPANY);
    }
    
    public String getName() {
        return (String) attributes.get(GitHubProfileDefinition.NAME);
    }
    
    public int getFollowingCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.FOLLOWING_COUNT));
    }
    
    /**
     * Indicate if the following_count attribute exists.
     * 
     * @return if the following_count attribute exists
     */
    public boolean isFollowingCountDefined() {
        return attributes.get(GitHubProfileDefinition.FOLLOWING_COUNT) != null;
    }
    
    public String getBlog() {
        return (String) attributes.get(GitHubProfileDefinition.BLOG);
    }
    
    public int getPublicRepoCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.PUBLIC_REPO_COUNT));
    }
    
    /**
     * Indicate if the public_repo_count attribute exists.
     * 
     * @return if the public_repo_count attribute exists
     */
    public boolean isPublicRepoCountDefined() {
        return attributes.get(GitHubProfileDefinition.PUBLIC_REPO_COUNT) != null;
    }
    
    public int getPublicGistCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.PUBLIC_GIST_COUNT));
    }
    
    /**
     * Indicate if the public_gist_count attribute exists.
     * 
     * @return if the public_gist_count attribute exists
     */
    public boolean isPublicGistCountDefined() {
        return attributes.get(GitHubProfileDefinition.PUBLIC_GIST_COUNT) != null;
    }
    
    public int getDiskUsage() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.DISK_USAGE));
    }
    
    /**
     * Indicate if the disk_usage attribute exists.
     * 
     * @return if the disk_usage attribute exists
     */
    public boolean isDiskUsageDefined() {
        return attributes.get(GitHubProfileDefinition.DISK_USAGE) != null;
    }
    
    public int getCollaborators() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.COLLABORATORS));
    }
    
    /**
     * Indicate if the collaborators attribute exists.
     * 
     * @return if the collaborators attribute exists
     */
    public boolean isCollaboratorsDefined() {
        return attributes.get(GitHubProfileDefinition.COLLABORATORS) != null;
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) attributes.get(GitHubProfileDefinition.PLAN);
    }
    
    public int getOwnedPrivateRepoCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.OWNED_PRIVATE_REPO_COUNT));
    }
    
    /**
     * Indicate if the owned_private_repo_count attribute exists.
     * 
     * @return if the owned_private_repo_count attribute exists
     */
    public boolean isOwnedPrivateRepoCountDefined() {
        return attributes.get(GitHubProfileDefinition.OWNED_PRIVATE_REPO_COUNT) != null;
    }
    
    public int getTotalPrivateRepoCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.TOTAL_PRIVATE_REPO_COUNT));
    }
    
    /**
     * Indicate if the total_private_repo_count attribute exists.
     * 
     * @return if the total_private_repo_count attribute exists
     */
    public boolean isTotalPrivateRepoCountDefined() {
        return attributes.get(GitHubProfileDefinition.TOTAL_PRIVATE_REPO_COUNT) != null;
    }
    
    public int getPrivateGistCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.PRIVATE_GIST_COUNT));
    }
    
    /**
     * Indicate if the private_gist_count attribute exists.
     * 
     * @return if the private_gist_count attribute exists
     */
    public boolean isPrivateGistCountDefined() {
        return attributes.get(GitHubProfileDefinition.PRIVATE_GIST_COUNT) != null;
    }
    
    public String getLogin() {
        return (String) attributes.get(GitHubProfileDefinition.LOGIN);
    }
    
    public int getFollowersCount() {
        return getSafeInteger((Integer) attributes.get(GitHubProfileDefinition.FOLLOWERS_COUNT));
    }
    
    /**
     * Indicate if the followers_count attribute exists.
     * 
     * @return if the followers_count attribute exists
     */
    public boolean isFollowersCountDefined() {
        return attributes.get(GitHubProfileDefinition.FOLLOWERS_COUNT) != null;
    }
    
    public Date getCreatedAt() {
        return (Date) attributes.get(GitHubProfileDefinition.CREATED_AT);
    }
    
    public String getEmail() {
        return (String) attributes.get(GitHubProfileDefinition.EMAIL);
    }
    
    public String getLocation() {
        return (String) attributes.get(GitHubProfileDefinition.LOCATION);
    }
    
    public String getType() {
        return (String) attributes.get(GitHubProfileDefinition.TYPE);
    }
    
    public String getPermission() {
        return (String) attributes.get(GitHubProfileDefinition.PERMISSION);
    }
    
    public String getGravatarId() {
        return (String) attributes.get(GitHubProfileDefinition.GRAVATAR_ID);
    }
}
