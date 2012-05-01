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

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.converter.Converters;

/**
 * This class defines the attributes of the GitHub profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubAttributesDefinition extends AttributesDefinition {
    
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
    
    public GitHubAttributesDefinition() {
        String[] names = new String[] {
            COMPANY, NAME, BLOG, LOGIN, EMAIL, LOCATION, TYPE, PERMISSION, GRAVATAR_ID
        };
        for (String name : names) {
            attributes.add(name);
            converters.put(name, Converters.stringConverter);
        }
        names = new String[] {
            FOLLOWING_COUNT, PUBLIC_REPO_COUNT, PUBLIC_GIST_COUNT, DISK_USAGE, COLLABORATORS, OWNED_PRIVATE_REPO_COUNT,
            TOTAL_PRIVATE_REPO_COUNT, PRIVATE_GIST_COUNT, FOLLOWERS_COUNT
        };
        for (String name : names) {
            attributes.add(name);
            converters.put(name, Converters.integerConverter);
        }
        attributes.add(CREATED_AT);
        converters.put(CREATED_AT, GitHubConverters.dateConverter);
        attributes.add(PLAN);
        converters.put(PLAN, GitHubConverters.planConverter);
    }
}
