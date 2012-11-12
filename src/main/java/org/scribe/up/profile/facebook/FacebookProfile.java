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
package org.scribe.up.profile.facebook;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.CommonProfile;
import org.scribe.up.profile.Gender;
import org.scribe.up.profile.OAuthAttributesDefinitions;
import org.scribe.up.profile.BaseOAuthProfile;

/**
 * This class is the user profile for Facebook with appropriate getters.<br />
 * It is returned by the {@link org.scribe.up.provider.impl.FacebookProvider}.
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
 * <td>the <i>first_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getFamilyName()</td>
 * <td>the <i>last_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getDisplayName()</td>
 * <td>the <i>name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getUsername()</td>
 * <td>the <i>username</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Gender getGender()</td>
 * <td>the <i>gender</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Locale getLocale()</td>
 * <td>the <i>locale</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPictureUrl()</td>
 * <td>the <i>url</i> sub-attribute of the <i>picture</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getProfileUrl()</td>
 * <td>the <i>link</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getLocation()</td>
 * <td>the <i>name</i> sub-attribute of the <i>location</i> attribute</td>
 * </tr>
 * <tr>
 * <th colspan="2">More specific attributes</th>
 * </tr>
 * <tr>
 * <td>String getMiddleName()</td>
 * <td>the <i>middle_name</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookObject&gt; getLanguages()</td>
 * <td>the <i>languages</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getThirdPartyId()</td>
 * <td>the <i>third_party_id</i> attribute</td>
 * </tr>
 * <tr>
 * <td>int getTimezone()</td>
 * <td>the <i>timezone</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isTimezoneDefined()</td>
 * <td>if the <i>timezone</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>Date getUpdateTime()</td>
 * <td>the <i>updated_time</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isVerified()</td>
 * <td>the <i>verified</i> attribute</td>
 * </tr>
 * <tr>
 * <td>boolean isVerifiedDefined()</td>
 * <td>if the <i>verified</i> attribute exists</td>
 * </tr>
 * <tr>
 * <td>String getBio()</td>
 * <td>the <i>bio</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getBirthday()</td>
 * <td>the <i>birthday</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookEducation&gt; getEducation()</td>
 * <td>the <i>education</i> attribute</td>
 * </tr>
 * <tr>
 * <td>FacebookObject getHometown()</td>
 * <td>the <i>hometown</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;String&gt; getInterestedIn()</td>
 * <td>the <i>interested_in</i> attribute</td>
 * </tr>
 * <tr>
 * <td>FacebookObject getLocationObject()</td>
 * <td>the <i>locatiion</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getPolitical()</td>
 * <td>the <i>political</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookObject&gt; getFavoriteAthletes()</td>
 * <td>the <i>favorite_athletes</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookObject&gt; getFavoriteTeams()</td>
 * <td>the <i>favorite_teams</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getQuotes()</td>
 * <td>the <i>quotes</i> attribute</td>
 * </tr>
 * <tr>
 * <td>FacebookRelationshipStatus getRelationshipStatus()</td>
 * <td>the <i>relationship_status</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getReligion()</td>
 * <td>the <i>religion</i> attribute</td>
 * </tr>
 * <tr>
 * <td>FacebookObject getSignificantOther()</td>
 * <td>the <i>significant_other</i> attribute</td>
 * </tr>
 * <tr>
 * <td>String getWebsite()</td>
 * <td>the <i>website</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookWork&gt; getWork()</td>
 * <td>the <i>work</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookObject&gt; getFriends()</td>
 * <td>the <i>friends</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookInfo&gt; getMovies()</td>
 * <td>the <i>movies</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookInfo&gt; getMusic()</td>
 * <td>the <i>music</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookInfo&gt; getBooks()</td>
 * <td>the <i>books</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookInfo&gt; getLikes()</td>
 * <td>the <i>likes</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookPhoto&gt; getAlbums()</td>
 * <td>the <i>albums</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookEvent&gt; getEvents()</td>
 * <td>the <i>events</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookGroup&gt; getGroups()</td>
 * <td>the <i>groups</i> attribute</td>
 * </tr>
 * <tr>
 * <td>List&lt;FacebookMusicListen&gt; getMusicListens()</td>
 * <td>the <i>music.listens</i> attribute</td>
 * </tr>
 * <tr>
 * <td>FacebookPicture getPicture()</td>
 * <td>the <i>picture</i> attribute</td>
 * </tr>
 * </table>
 * 
 * @see org.scribe.up.provider.impl.FacebookProvider
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends BaseOAuthProfile implements CommonProfile {
    
    private static final long serialVersionUID = 743506441847600518L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.facebookDefinition;
    }
    
    public String getEmail() {
        return (String) this.attributes.get(FacebookAttributesDefinition.EMAIL);
    }
    
    public String getFirstName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.FIRST_NAME);
    }
    
    public String getFamilyName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.LAST_NAME);
    }
    
    public String getDisplayName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.NAME);
    }
    
    public String getUsername() {
        return (String) this.attributes.get(FacebookAttributesDefinition.USERNAME);
    }
    
    public Gender getGender() {
        return (Gender) this.attributes.get(FacebookAttributesDefinition.GENDER);
    }
    
    public Locale getLocale() {
        return (Locale) this.attributes.get(FacebookAttributesDefinition.LOCALE);
    }
    
    public String getPictureUrl() {
        final FacebookPicture picture = (FacebookPicture) this.attributes.get(FacebookAttributesDefinition.PICTURE);
        if (picture != null) {
            return picture.getUrl();
        }
        return null;
    }
    
    public String getProfileUrl() {
        return (String) this.attributes.get(FacebookAttributesDefinition.LINK);
    }
    
    public String getLocation() {
        final FacebookObject location = (FacebookObject) this.attributes.get(FacebookAttributesDefinition.LOCATION);
        if (location != null) {
            return location.getName();
        }
        return null;
    }
    
    public String getMiddleName() {
        return (String) this.attributes.get(FacebookAttributesDefinition.MIDDLE_NAME);
    }
    
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.LANGUAGES);
    }
    
    public String getThirdPartyId() {
        return (String) this.attributes.get(FacebookAttributesDefinition.THIRD_PARTY_ID);
    }
    
    public int getTimezone() {
        return getSafeInt((Integer) this.attributes.get(FacebookAttributesDefinition.TIMEZONE));
    }
    
    public boolean isTimezoneDefined() {
        return this.attributes.get(FacebookAttributesDefinition.TIMEZONE) != null;
    }
    
    public Date getUpdateTime() {
        return (Date) this.attributes.get(FacebookAttributesDefinition.UPDATED_TIME);
    }
    
    public boolean isVerified() {
        return getSafeBoolean((Boolean) this.attributes.get(FacebookAttributesDefinition.VERIFIED));
    }
    
    public boolean isVerifiedDefined() {
        return this.attributes.get(FacebookAttributesDefinition.VERIFIED) != null;
    }
    
    public String getBio() {
        return (String) this.attributes.get(FacebookAttributesDefinition.BIO);
    }
    
    public Date getBirthday() {
        return (Date) this.attributes.get(FacebookAttributesDefinition.BIRTHDAY);
    }
    
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) this.attributes.get(FacebookAttributesDefinition.EDUCATION);
    }
    
    public FacebookObject getHometown() {
        return (FacebookObject) this.attributes.get(FacebookAttributesDefinition.HOMETOWN);
    }
    
    public List<String> getInterestedIn() {
        return (List<String>) this.attributes.get(FacebookAttributesDefinition.INTERESTED_IN);
    }
    
    public FacebookObject getLocationObject() {
        return (FacebookObject) this.attributes.get(FacebookAttributesDefinition.LOCATION);
    }
    
    public String getPolitical() {
        return (String) this.attributes.get(FacebookAttributesDefinition.POLITICAL);
    }
    
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.FAVORITE_ATHLETES);
    }
    
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.FAVORITE_TEAMS);
    }
    
    public String getQuotes() {
        return (String) this.attributes.get(FacebookAttributesDefinition.QUOTES);
    }
    
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) this.attributes.get(FacebookAttributesDefinition.RELATIONSHIP_STATUS);
    }
    
    public String getReligion() {
        return (String) this.attributes.get(FacebookAttributesDefinition.RELIGION);
    }
    
    public FacebookObject getSignificantOther() {
        return (FacebookObject) this.attributes.get(FacebookAttributesDefinition.SIGNIFICANT_OTHER);
    }
    
    public String getWebsite() {
        return (String) this.attributes.get(FacebookAttributesDefinition.WEBSITE);
    }
    
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) this.attributes.get(FacebookAttributesDefinition.WORK);
    }
    
    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) this.attributes.get(FacebookAttributesDefinition.FRIENDS);
    }
    
    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.MOVIES);
    }
    
    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.MUSIC);
    }
    
    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.BOOKS);
    }
    
    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) this.attributes.get(FacebookAttributesDefinition.LIKES);
    }
    
    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) this.attributes.get(FacebookAttributesDefinition.ALBUMS);
    }
    
    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) this.attributes.get(FacebookAttributesDefinition.EVENTS);
    }
    
    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) this.attributes.get(FacebookAttributesDefinition.GROUPS);
    }
    
    public List<FacebookMusicListen> getMusicListens() {
        return (List<FacebookMusicListen>) this.attributes.get(FacebookAttributesDefinition.MUSIC_LISTENS);
    }
    
    public FacebookPicture getPicture() {
        return (FacebookPicture) this.attributes.get(FacebookAttributesDefinition.PICTURE);
    }
}
