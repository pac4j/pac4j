/*
  Copyright 2012 Jérôme Leleu

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
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.UserProfileHelper;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the GitHub provider to authenticate user in GitHub. It extends the <b>BaseOAuth20Provider</b> class.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class GitHubProvider extends BaseOAuth20Provider {
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(GitHubApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .scope("user").build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "https://github.com/api/v2/json/user/show";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        UserProfile userProfile = new UserProfile();
        try {
            JsonNode json = UserProfileHelper.getFirstNode(body);
            json = json.get("user");
            UserProfileHelper.addIdentifier(userProfile, json, "id");
            UserProfileHelper.addAttribute(userProfile, json, "gravatar_id");
            UserProfileHelper.addAttribute(userProfile, json, "company");
            UserProfileHelper.addAttribute(userProfile, json, "name");
            UserProfileHelper.addAttribute(userProfile, json, "created_at");
            UserProfileHelper.addAttribute(userProfile, json, "location");
            UserProfileHelper.addAttribute(userProfile, json, "disk_usage");
            UserProfileHelper.addAttribute(userProfile, json, "collaborators");
            UserProfileHelper.addAttribute(userProfile, json, "public_repo_count");
            UserProfileHelper.addAttribute(userProfile, json, "public_gist_count");
            UserProfileHelper.addAttribute(userProfile, json, "blog");
            UserProfileHelper.addAttribute(userProfile, json, "following_count");
            UserProfileHelper.addAttribute(userProfile, json, "owned_private_repo_count");
            UserProfileHelper.addAttribute(userProfile, json, "private_gist_count");
            UserProfileHelper.addAttribute(userProfile, json, "type");
            UserProfileHelper.addAttribute(userProfile, json, "permission");
            UserProfileHelper.addAttribute(userProfile, json, "total_private_repo_count");
            UserProfileHelper.addAttribute(userProfile, json, "followers_count");
            UserProfileHelper.addAttribute(userProfile, json, "login");
            UserProfileHelper.addAttribute(userProfile, json, "email");
        } catch (RuntimeException e) {
            logger.error("RuntimeException", e);
        }
        return userProfile;
    }
}
