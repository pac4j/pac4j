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
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.github.GitHubProfile;
import org.scribe.up.provider.BaseOAuth20Provider;

/**
 * This class is the OAuth provider to authenticate user in GitHub. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.github.GitHubProfile} : company (String), name (String), following_count
 * (Integer), blog (String), public_repo_count (Integer), public_gist_count (Integer), disk_usage (Integer), collaborators (Integer), plan
 * (GitHubPlan), owned_private_repo_count (Integer), total_private_repo_count (Integer), private_gist_count (Integer), login (String),
 * followers_count (Integer), created_at (FormattedDate), email (String), location (String), type (String), permission (String) and
 * gravatar_id (String)
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 * @see http://develop.github.com/p/general.html
 */
public class GitHubProvider extends BaseOAuth20Provider {
    
    public final static String TYPE = GitHubProvider.class.getSimpleName();
    
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
        GitHubProfile profile = new GitHubProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = json.get("user");
            if (json != null) {
                profile.setId(JsonHelper.get(json, "id"));
                for (String attribute : AttributesDefinitions.githubDefinition.getAttributes()) {
                    profile.addAttribute(attribute, JsonHelper.get(json, attribute));
                }
            }
        }
        return profile;
    }
}
