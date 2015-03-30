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

import java.util.Date;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for GitHub with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.GitHubClient}.</p>
 * <table summary="" border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.pac4j.core.profile.CommonProfile}</th>
 * </tr>
 * <tr>
 * <td>String getEmail()</td>
 * <td>the <i>email</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFirstName()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>login</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>{@link org.pac4j.core.profile.Gender#UNSPECIFIED}</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>the <i>avatar_url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>html_url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>location</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getCompany()</td>
 * <td>the <i>company</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getFollowing()</td>
 * <td>the <i>following</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getBlog()</td>
 * <td>the <i>blog</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getPublicRepos()</td>
 * <td>the <i>public_repos</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getPublicGists()</td>
 * <td>the <i>public_gists</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getDiskUsage()</td>
 * <td>the <i>disk_usage</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getCollaborators()</td>
 * <td>the <i>collaborators</i> attribute</td>
 * </tr>
 * <tr>
 * <td>GitHubPlan getPlan()</td>
 * <td>the <i>plan</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getOwnedPrivateRepos()</td>
 * <td>the <i>owned_private_repos</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getTotalPrivateRepos()</td>
 * <td>the <i>total_private_repos</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getPrivateGists()</td>
 * <td>the <i>private_gists</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Integer getFollowers()</td>
 * <td>the <i>followers</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getCreatedAt()</td>
 * <td>the <i>created_at</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getType()</td>
 * <td>the <i>type</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getGravatarId()</td>
 * <td>the <i>gravatar_id</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUrl()</td>
 * <td>the <i>url</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getHireable()</td>
 * <td>the <i>hireable</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getBio()</td>
 * <td>the <i>bio</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.pac4j.oauth.client.GitHubClient
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = -8030906034414268058L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.githubDefinition;
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(GitHubAttributesDefinition.NAME);
    }
    
    @Override
    public String getUsername() {
        return (String) getAttribute(GitHubAttributesDefinition.LOGIN);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(GitHubAttributesDefinition.AVATAR_URL);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(GitHubAttributesDefinition.HTML_URL);
    }
    
    public String getCompany() {
        return (String) getAttribute(GitHubAttributesDefinition.COMPANY);
    }
    
    public Integer getFollowing() {
        return (Integer) getAttribute(GitHubAttributesDefinition.FOLLOWING);
    }
    
    public String getBlog() {
        return (String) getAttribute(GitHubAttributesDefinition.BLOG);
    }
    
    public Integer getPublicRepos() {
        return (Integer) getAttribute(GitHubAttributesDefinition.PUBLIC_REPOS);
    }
    
    public Integer getPublicGists() {
        return (Integer) getAttribute(GitHubAttributesDefinition.PUBLIC_GISTS);
    }
    
    public Integer getDiskUsage() {
        return (Integer) getAttribute(GitHubAttributesDefinition.DISK_USAGE);
    }
    
    public Integer getCollaborators() {
        return (Integer) getAttribute(GitHubAttributesDefinition.COLLABORATORS);
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) getAttribute(GitHubAttributesDefinition.PLAN);
    }
    
    public Integer getOwnedPrivateRepos() {
        return (Integer) getAttribute(GitHubAttributesDefinition.OWNED_PRIVATE_REPOS);
    }
    
    public Integer getTotalPrivateRepos() {
        return (Integer) getAttribute(GitHubAttributesDefinition.TOTAL_PRIVATE_REPOS);
    }
    
    public Integer getPrivateGists() {
        return (Integer) getAttribute(GitHubAttributesDefinition.PRIVATE_GISTS);
    }
    
    public Integer getFollowers() {
        return (Integer) getAttribute(GitHubAttributesDefinition.FOLLOWERS);
    }
    
    public Date getCreatedAt() {
        return (Date) getAttribute(GitHubAttributesDefinition.CREATED_AT);
    }

    public Date getUpdatedAt() {
      return (Date) getAttribute(GitHubAttributesDefinition.UPDATED_AT);
    }

    public String getType() {
        return (String) getAttribute(GitHubAttributesDefinition.TYPE);
    }
    
    public String getGravatarId() {
        return (String) getAttribute(GitHubAttributesDefinition.GRAVATAR_ID);
    }
    
    public String getUrl() {
        return (String) getAttribute(GitHubAttributesDefinition.URL);
    }
    
    public Boolean getHireable() {
        return (Boolean) getAttribute(GitHubAttributesDefinition.HIREABLE);
    }
    
    public String getBio() {
        return (String) getAttribute(GitHubAttributesDefinition.BIO);
    }
}
