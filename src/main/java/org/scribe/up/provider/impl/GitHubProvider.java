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
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the OAuth provider to authenticate user in GitHub.
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
            "gravatar_id", "company", "name", "created_at", "location", "disk_usage", "collaborators",
            "public_repo_count", "public_gist_count", "blog", "following_count", "owned_private_repo_count",
            "private_gist_count", "type", "permission", "total_private_repo_count", "followers_count", "login", "email"
        };
        for (String name : names) {
            mainAttributes.put(name, null);
        }
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://github.com/api/v2/json/user/show";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get("user");
            if (json != null) {
                profileHelper.addIdentifier(userProfile, json, "id");
                for (String attribute : mainAttributes.keySet()) {
                    profileHelper.addAttribute(userProfile, json, attribute, mainAttributes.get(attribute));
                }
            }
        }
        return userProfile;
    }
}
