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

import org.scribe.up.profile.UserProfile;
import org.scribe.up.util.ObjectHelper;

/**
 * This class is the user profile for GitHub with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfile extends UserProfile {
    
    public static final String ID = "id";
    public static final String USER = "user";
    public static final String COMPANY = "company";
    public static final String NAME = "name";
    public static final String FOLLOWING_COUNT = "following_count";
    public static final String BLOG = "blog";
    public static final String PUBLIC_REPO_COUNT = "public_repo_count";
    public static final String PUBLIC_GIST_COUNT = "public_gist_count";
    public static final String DISK_USAGE = "disk_usage";
    public static final String COLLABORATORS = "collaborators";
    public static final String PLAN = "plan";
    public static final String OWNED_PRIVATE_REPO_COUNT = "owned_private_repo_count";
    public static final String TOTAL_PRIVATE_REPO_COUNT = "total_private_repo_count";
    public static final String PRIVATE_GIST_COUNT = "private_gist_count";
    public static final String LOGIN = "login";
    public static final String FOLLOWERS_COUNT = "followers_count";
    public static final String CREATED_AT = "created_at";
    public static final String EMAIL = "email";
    public static final String LOCATION = "location";
    public static final String TYPE = "type";
    public static final String PERMISSION = "permission";
    public static final String GRAVATAR_ID = "gravatar_id";
    
    public GitHubProfile() {
        super();
    }
    
    public GitHubProfile(String id) {
        super(id);
    }
    
    public GitHubProfile(String id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getCompany() {
        return (String) attributes.get(COMPANY);
    }
    
    public String getName() {
        return (String) attributes.get(NAME);
    }
    
    public int getFollowingCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(FOLLOWING_COUNT), 0);
    }
    
    public String getBlog() {
        return (String) attributes.get(BLOG);
    }
    
    public int getPublicRepoCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(PUBLIC_REPO_COUNT), 0);
    }
    
    public int getPublicGistCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(PUBLIC_GIST_COUNT), 0);
    }
    
    public int getDiskUsage() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(DISK_USAGE), 0);
    }
    
    public int getCollaborators() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(COLLABORATORS), 0);
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) attributes.get(PLAN);
    }
    
    public int getOwnedPrivateRepoCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(OWNED_PRIVATE_REPO_COUNT), 0);
    }
    
    public int getTotalPrivateRepoCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(TOTAL_PRIVATE_REPO_COUNT), 0);
    }
    
    public int getPrivateGistCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(PRIVATE_GIST_COUNT), 0);
    }
    
    public String getLogin() {
        return (String) attributes.get(LOGIN);
    }
    
    public int getFollowersCount() {
        return (Integer) ObjectHelper.getDefaultIfNull(attributes.get(FOLLOWERS_COUNT), 0);
    }
    
    public Date getCreatedAt() {
        return (Date) attributes.get(CREATED_AT);
    }
    
    public String getEmail() {
        return (String) attributes.get(EMAIL);
    }
    
    public String getLocation() {
        return (String) attributes.get(LOCATION);
    }
    
    public String getType() {
        return (String) attributes.get(TYPE);
    }
    
    public String getPermission() {
        return (String) attributes.get(PERMISSION);
    }
    
    public String getGravatarId() {
        return (String) attributes.get(GRAVATAR_ID);
    }
}
