/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.oauth.profile.github;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the GitHub profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String TYPE = "type";
    public static final String BLOG = "blog";
    public static final String URL = "url";
    public static final String PUBLIC_GISTS = "public_gists";
    public static final String FOLLOWING = "following";
    public static final String PRIVATE_GISTS = "private_gists";
    public static final String PUBLIC_REPOS = "public_repos";
    public static final String GRAVATAR_ID = "gravatar_id";
    public static final String AVATAR_URL = "avatar_url";
    public static final String FOLLOWERS = "followers";
    public static final String LOGIN = "login";
    public static final String COMPANY = "company";
    public static final String EMAIL = "email";
    public static final String HIREABLE = "hireable";
    public static final String COLLABORATORS = "collaborators";
    public static final String HTML_URL = "html_url";
    public static final String BIO = "bio";
    public static final String TOTAL_PRIVATE_REPOS = "total_private_repos";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";
    public static final String NAME = "name";
    public static final String DISK_USAGE = "disk_usage";
    public static final String PLAN = "plan";
    public static final String OWNED_PRIVATE_REPOS = "owned_private_repos";
    public static final String LOCATION = "location";
    
    public GitHubAttributesDefinition() {
        String[] names = new String[] {
            URL, COMPANY, NAME, BLOG, LOGIN, EMAIL, LOCATION, TYPE, GRAVATAR_ID, AVATAR_URL, HTML_URL, BIO
        };
        for (final String name : names) {
            addAttribute(name, Converters.stringConverter);
        }
        names = new String[] {
            FOLLOWING, PUBLIC_REPOS, PUBLIC_GISTS, DISK_USAGE, COLLABORATORS, OWNED_PRIVATE_REPOS, TOTAL_PRIVATE_REPOS,
            PRIVATE_GISTS, FOLLOWERS
        };
        for (final String name : names) {
            addAttribute(name, Converters.integerConverter);
        }
        addAttribute(HIREABLE, Converters.booleanConverter);
        addAttribute(CREATED_AT, GitHubConverters.dateConverter);
        addAttribute(UPDATED_AT, GitHubConverters.dateConverter);
        addAttribute(PLAN, GitHubConverters.planConverter);
    }
}
