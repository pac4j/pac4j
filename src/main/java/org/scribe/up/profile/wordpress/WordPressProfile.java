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
package org.scribe.up.profile.wordpress;

import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthProfile;

/**
 * This class is the user profile for WordPress with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.WordPressProvider}.
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
 * <td>the <i>display_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>username</i> attribute</td>
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
 * <td>the <i>avatar_URL</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>profile_URL</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>int getPrimaryBlog()</td>
 * <td>the <i>primary_blog</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isPrimaryBlogDefined()</td>
 * <td>if the <i>primary_blog</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>WordPressLinks getLinks()</td>
 * <td>the <i>links</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.scribe.up.provider.impl.WordPressProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfile extends OAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = 5325534468726215038L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.wordPressDefinition;
    }
    
    public WordPressProfile() {
        super();
    }
    
    public WordPressProfile(final Object id) {
        super(id);
    }
    
    public WordPressProfile(final Object id, final Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getEmail() {
        return (String) this.attributes.get(WordPressAttributesDefinition.EMAIL);
    }
    
    public String getFirstName() {
        return null;
    }
    
    public String getFamilyName() {
        return null;
    }
    
    public String getDisplayName() {
        return (String) this.attributes.get(WordPressAttributesDefinition.DISPLAY_NAME);
    }
    
    public String getUsername() {
        return (String) this.attributes.get(WordPressAttributesDefinition.USERNAME);
    }
    
    public Gender getGender() {
        return Gender.UNSPECIFIED;
    }
    
    public Locale getLocale() {
        return null;
    }
    
    public String getPictureUrl() {
        return (String) this.attributes.get(WordPressAttributesDefinition.AVATAR_URL);
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(WordPressAttributesDefinition.PROFILE_URL);
    }
    
    public String getLocation() {
        return null;
    }
    
    public int getPrimaryBlog() {
        return getSafeInt((Integer) this.attributes.get(WordPressAttributesDefinition.PRIMARY_BLOG));
    }
    
    public boolean isPrimaryBlogDefined() {
        return this.attributes.get(WordPressAttributesDefinition.PRIMARY_BLOG) != null;
    }
    
    public WordPressLinks getLinks() {
        return (WordPressLinks) this.attributes.get(WordPressAttributesDefinition.LINKS);
    }
}
