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
package org.pac4j.oauth.profile.facebook;

import java.util.Date;
import java.util.List;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for Facebook with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FacebookClient}.</p>
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
 * <td>Integer getTimezone()</td>
 * <td>the <i>timezone</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Date getUpdateTime()</td>
 * <td>the <i>updated_time</i> attribute</td>
 * </tr>
 * <tr>
 * <td>Boolean getVerified()</td>
 * <td>the <i>verified</i> attribute</td>
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
 * @see org.pac4j.oauth.client.FacebookClient
 * @author Jerome Leleu
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public class FacebookProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = 6339376303764855109L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.facebookDefinition;
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(FacebookAttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getDisplayName() {
        return (String) getAttribute(FacebookAttributesDefinition.NAME);
    }

    /**
     * For new OAuth Facebook applications, the username field is no more supported and generates an error:
     * {"error":{"message":"(#12) username is deprecated for versions v2.0 and higher","type":"OAuthException","code":12}}
     */
    @Deprecated
    public String getUsername() {
        return (String) getAttribute(USERNAME);
    }

    @Override
    public String getPictureUrl() {
        final FacebookPicture picture = (FacebookPicture) getAttribute(FacebookAttributesDefinition.PICTURE);
        if (picture != null) {
            return picture.getUrl();
        }
        return null;
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(FacebookAttributesDefinition.LINK);
    }
    
    @Override
    public String getLocation() {
        final FacebookObject location = (FacebookObject) getAttribute(FacebookAttributesDefinition.LOCATION);
        if (location != null) {
            return location.getName();
        }
        return null;
    }
    
    public String getMiddleName() {
        return (String) getAttribute(FacebookAttributesDefinition.MIDDLE_NAME);
    }
    
    public List<FacebookObject> getLanguages() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.LANGUAGES);
    }
    
    public String getThirdPartyId() {
        return (String) getAttribute(FacebookAttributesDefinition.THIRD_PARTY_ID);
    }
    
    public Integer getTimezone() {
        return (Integer) getAttribute(FacebookAttributesDefinition.TIMEZONE);
    }
    
    public Date getUpdateTime() {
        return (Date) getAttribute(FacebookAttributesDefinition.UPDATED_TIME);
    }
    
    public Boolean getVerified() {
        return (Boolean) getAttribute(FacebookAttributesDefinition.VERIFIED);
    }
    
    public String getBio() {
        return (String) getAttribute(FacebookAttributesDefinition.BIO);
    }
    
    public Date getBirthday() {
        return (Date) getAttribute(FacebookAttributesDefinition.BIRTHDAY);
    }
    
    public List<FacebookEducation> getEducation() {
        return (List<FacebookEducation>) getAttribute(FacebookAttributesDefinition.EDUCATION);
    }
    
    public FacebookObject getHometown() {
        return (FacebookObject) getAttribute(FacebookAttributesDefinition.HOMETOWN);
    }
    
    public List<String> getInterestedIn() {
        return (List<String>) getAttribute(FacebookAttributesDefinition.INTERESTED_IN);
    }
    
    public FacebookObject getLocationObject() {
        return (FacebookObject) getAttribute(FacebookAttributesDefinition.LOCATION);
    }
    
    public String getPolitical() {
        return (String) getAttribute(FacebookAttributesDefinition.POLITICAL);
    }
    
    public List<FacebookObject> getFavoriteAthletes() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.FAVORITE_ATHLETES);
    }
    
    public List<FacebookObject> getFavoriteTeams() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.FAVORITE_TEAMS);
    }
    
    public String getQuotes() {
        return (String) getAttribute(FacebookAttributesDefinition.QUOTES);
    }
    
    public FacebookRelationshipStatus getRelationshipStatus() {
        return (FacebookRelationshipStatus) getAttribute(FacebookAttributesDefinition.RELATIONSHIP_STATUS);
    }
    
    public String getReligion() {
        return (String) getAttribute(FacebookAttributesDefinition.RELIGION);
    }
    
    public FacebookObject getSignificantOther() {
        return (FacebookObject) getAttribute(FacebookAttributesDefinition.SIGNIFICANT_OTHER);
    }
    
    public String getWebsite() {
        return (String) getAttribute(FacebookAttributesDefinition.WEBSITE);
    }
    
    public List<FacebookWork> getWork() {
        return (List<FacebookWork>) getAttribute(FacebookAttributesDefinition.WORK);
    }
    
    public List<FacebookObject> getFriends() {
        return (List<FacebookObject>) getAttribute(FacebookAttributesDefinition.FRIENDS);
    }
    
    public List<FacebookInfo> getMovies() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.MOVIES);
    }
    
    public List<FacebookInfo> getMusic() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.MUSIC);
    }
    
    public List<FacebookInfo> getBooks() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.BOOKS);
    }
    
    public List<FacebookInfo> getLikes() {
        return (List<FacebookInfo>) getAttribute(FacebookAttributesDefinition.LIKES);
    }
    
    public List<FacebookPhoto> getAlbums() {
        return (List<FacebookPhoto>) getAttribute(FacebookAttributesDefinition.ALBUMS);
    }
    
    public List<FacebookEvent> getEvents() {
        return (List<FacebookEvent>) getAttribute(FacebookAttributesDefinition.EVENTS);
    }
    
    public List<FacebookGroup> getGroups() {
        return (List<FacebookGroup>) getAttribute(FacebookAttributesDefinition.GROUPS);
    }
    
    public List<FacebookMusicListen> getMusicListens() {
        return (List<FacebookMusicListen>) getAttribute(FacebookAttributesDefinition.MUSIC_LISTENS);
    }
    
    public FacebookPicture getPicture() {
        return (FacebookPicture) getAttribute(FacebookAttributesDefinition.PICTURE);
    }
}
