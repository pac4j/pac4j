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
package org.scribe.up.provider.impl;

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.up.builder.api.GitHubApi;
import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.profile.github.GitHubPlan;
import org.scribe.up.profile.github.GitHubProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the OAuth provider to authenticate user in GitHub. Scope is not used. Attributes are defined at
 * http://develop.github.com/p/general.html.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.github.GitHubProfile} : company (String), name (String), following_count
 * (Integer), blog (String), public_repo_count (Integer), public_gist_count (Integer), disk_usage (Integer), collaborators (Integer), plan (
 * {@link org.scribe.up.profile.github.GitHubPlan}), owned_private_repo_count (Integer), total_private_repo_count (Integer),
 * private_gist_count (Integer), login (String), followers_count (Integer), created_at (Date), email (String), location (String), type
 * (String), permission (String) and gravatar_id (String)
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class GitHubProvider extends BaseOAuth20Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(GitHubApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .scope("user").build();
        String[] names = new String[] {
            GitHubProfile.COMPANY, GitHubProfile.NAME, GitHubProfile.FOLLOWING_COUNT, GitHubProfile.BLOG,
            GitHubProfile.PUBLIC_REPO_COUNT, GitHubProfile.PUBLIC_GIST_COUNT, GitHubProfile.DISK_USAGE,
            GitHubProfile.COLLABORATORS, GitHubProfile.OWNED_PRIVATE_REPO_COUNT,
            GitHubProfile.TOTAL_PRIVATE_REPO_COUNT, GitHubProfile.PRIVATE_GIST_COUNT, GitHubProfile.LOGIN,
            GitHubProfile.FOLLOWERS_COUNT, GitHubProfile.EMAIL, GitHubProfile.LOCATION, GitHubProfile.TYPE,
            GitHubProfile.PERMISSION, GitHubProfile.GRAVATAR_ID
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
        mainAttributes.put(GitHubProfile.CREATED_AT, new DateConverter("yyyy/MM/dd HH:mm:ss z"));
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://github.com/api/v2/json/user/show";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        GitHubProfile profile = new GitHubProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get(GitHubProfile.USER);
            if (json != null) {
                UserProfileHelper.addIdentifier(profile, json, GitHubProfile.ID);
                for (String attribute : mainAttributes.keySet()) {
                    UserProfileHelper.addAttribute(profile, json, attribute, mainAttributes.get(attribute));
                }
                // plan
                JsonNode subJson = json.get(GitHubProfile.PLAN);
                if (subJson != null) {
                    UserProfileHelper.addAttribute(profile, GitHubProfile.PLAN, new GitHubPlan(subJson));
                }
            }
        }
        return profile;
    }
}
