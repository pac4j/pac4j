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
import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthProfile;

/**
 * This class is the user profile for GitHub with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.GitHubProvider}.
 * <p />
 * <table border="1" cellspacing="2px">
 * <tr>
 * <th>Method :</th>
 * <th>From the JSON profile response :</th>
 * </tr>
 * <tr>
 * <th colspan="2">The attributes of the {@link org.scribe.up.profile.CommonProfile}</th>
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
 * <td>{@link org.scribe.up.profile.Gender#UNSPECIFIED}</td>
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
 * <td>int getFollowing()</td>
 * <td>the <i>following</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowingDefined()</td>
 * <td>if the <i>following</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>String getBlog()</td>
 * <td>the <i>blog</i> attribute</td>
 * </tr>
 * <tr>
 * <td>int getPublicRepos()</td>
 * <td>the <i>public_repos</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isPublicReposDefined()</td>
 * <td>if the <i>public_repos</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getPublicGists()</td>
 * <td>the <i>public_gists</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isPublicGistsDefined()</td>
 * <td>if the <i>public_gists</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getDiskUsage()</td>
 * <td>the <i>disk_usage</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isDiskUsageDefined()</td>
 * <td>if the <i>disk_usage</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getCollaborators()</td>
 * <td>the <i>collaborators</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isCollaboratorsDefined()</td>
 * <td>if the <i>collaborators</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>GitHubPlan getPlan()</td>
 * <td>the <i>plan</i> attribute</td>
 * </tr>
 * <tr>
 * <td>int getOwnedPrivateRepos()</td>
 * <td>the <i>owned_private_repos</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isOwnedPrivateReposDefined()</td>
 * <td>if the <i>owned_private_repos</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getTotalPrivateRepos()</td>
 * <td>the <i>total_private_repos</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isTotalPrivateReposDefined()</td>
 * <td>if the <i>total_private_repos</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getPrivateGists()</td>
 * <td>the <i>private_gists</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isPrivateGistsDefined()</td>
 * <td>if the <i>private_gists</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>int getFollowers()</td>
 * <td>the <i>followers</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isFollowersDefined()</td>
 * <td>if the <i>followers</i> attribute exists</td>
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
 * <td>boolean isHireable()</td>
 * <td>the <i>hireable</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isHireableDefined()</td>
 * <td>if the <i>hireable</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>String getBio()</td>
 * <td>the <i>bio</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.scribe.up.provider.impl.GitHubProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class GitHubProfile extends OAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = 7630103875067480874L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.githubDefinition;
    }
    
    public GitHubProfile() {
        super();
    }
    
    public GitHubProfile(final Object id) {
        super(id);
    }
    
    public GitHubProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        return (String) this.attributes.get(GitHubAttributesDefinition.EMAIL);
    }
    
    public String getFirstName() {
        return null;
    }
    
    public String getFamilyName() {
        return null;
    }
    
    public String getDisplayName() {
        return (String) this.attributes.get(GitHubAttributesDefinition.NAME);
    }
    
    public String getUsername() {
        return (String) this.attributes.get(GitHubAttributesDefinition.LOGIN);
    }
    
    public Gender getGender() {
        return Gender.UNSPECIFIED;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public String getPictureUrl() {
        return (String) this.attributes.get(GitHubAttributesDefinition.AVATAR_URL);
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(GitHubAttributesDefinition.HTML_URL);
    }
    
    public String getLocation() {
        return (String) this.attributes.get(GitHubAttributesDefinition.LOCATION);
    }
    
    public String getCompany() {
        return (String) this.attributes.get(GitHubAttributesDefinition.COMPANY);
    }
    
    public int getFollowing() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.FOLLOWING));
    }
    
    public boolean isFollowingDefined() {
        return this.attributes.get(GitHubAttributesDefinition.FOLLOWING) != null;
    }
    
    public String getBlog() {
        return (String) this.attributes.get(GitHubAttributesDefinition.BLOG);
    }
    
    public int getPublicRepos() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.PUBLIC_REPOS));
    }
    
    public boolean isPublicReposDefined() {
        return this.attributes.get(GitHubAttributesDefinition.PUBLIC_REPOS) != null;
    }
    
    public int getPublicGists() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.PUBLIC_GISTS));
    }
    
    public boolean isPublicGistsDefined() {
        return this.attributes.get(GitHubAttributesDefinition.PUBLIC_GISTS) != null;
    }
    
    public int getDiskUsage() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.DISK_USAGE));
    }
    
    public boolean isDiskUsageDefined() {
        return this.attributes.get(GitHubAttributesDefinition.DISK_USAGE) != null;
    }
    
    public int getCollaborators() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.COLLABORATORS));
    }
    
    public boolean isCollaboratorsDefined() {
        return this.attributes.get(GitHubAttributesDefinition.COLLABORATORS) != null;
    }
    
    public GitHubPlan getPlan() {
        return (GitHubPlan) this.attributes.get(GitHubAttributesDefinition.PLAN);
    }
    
    public int getOwnedPrivateRepos() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.OWNED_PRIVATE_REPOS));
    }
    
    public boolean isOwnedPrivateReposDefined() {
        return this.attributes.get(GitHubAttributesDefinition.OWNED_PRIVATE_REPOS) != null;
    }
    
    public int getTotalPrivateRepos() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.TOTAL_PRIVATE_REPOS));
    }
    
    public boolean isTotalPrivateReposDefined() {
        return this.attributes.get(GitHubAttributesDefinition.TOTAL_PRIVATE_REPOS) != null;
    }
    
    public int getPrivateGists() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.PRIVATE_GISTS));
    }
    
    public boolean isPrivateGistsDefined() {
        return this.attributes.get(GitHubAttributesDefinition.PRIVATE_GISTS) != null;
    }
    
    public int getFollowers() {
        return getSafeInt((Integer) this.attributes.get(GitHubAttributesDefinition.FOLLOWERS));
    }
    
    public boolean isFollowersDefined() {
        return this.attributes.get(GitHubAttributesDefinition.FOLLOWERS) != null;
    }
    
    public Date getCreatedAt() {
        return (Date) this.attributes.get(GitHubAttributesDefinition.CREATED_AT);
    }
    
    public String getType() {
        return (String) this.attributes.get(GitHubAttributesDefinition.TYPE);
    }
    
    public String getGravatarId() {
        return (String) this.attributes.get(GitHubAttributesDefinition.GRAVATAR_ID);
    }
    
    public String getUrl() {
        return (String) this.attributes.get(GitHubAttributesDefinition.URL);
    }
    
    public boolean isHireable() {
        return getSafeBoolean((Boolean) this.attributes.get(GitHubAttributesDefinition.HIREABLE));
    }
    
    public boolean isHireableDefined() {
        return this.attributes.get(GitHubAttributesDefinition.HIREABLE) != null;
    }
    
    public String getBio() {
        return (String) this.attributes.get(GitHubAttributesDefinition.BIO);
    }
}
