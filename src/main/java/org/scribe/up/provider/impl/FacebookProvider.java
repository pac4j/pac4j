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
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.facebook.FacebookAttributesDefinition;
import org.scribe.up.profile.facebook.FacebookProfile;
import org.scribe.up.provider.BaseOAuth20Provider;
import org.scribe.up.util.StringHelper;

/**
 * This class is the OAuth provider to authenticate user in Facebook. Specific scopes and parameters (friendsReturned, moviesReturned,
 * musicReturned, booksReturned, likesReturned) can be defined to get more attributes.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.facebook.FacebookProfile} : name (String), first_name (String),
 * middle_name (String), last_name (String), gender (Gender), locale (Locale), languages (JsonList&lt;FacebookObject&gt;), link (String),
 * username (String), third_party_id (String), timezone (Integer), updated_time (FormattedDate), verified (Boolean), bio (String), birthday
 * (FormattedDate), education (JsonList&lt;FacebookEducation&gt;), email (String), hometown (FacebookObject), interested_in
 * (JsonList&lt;String&gt;), location (FacebookObject), political (String), favorite_athletes (JsonList&lt;FacebookObject&gt;),
 * favorite_teams (JsonList&lt;FacebookObject&gt;), quotes (String), relationship_status (FacebookRelationshipStatus), religion (String),
 * significant_other (FacebookObject), website (String), work (JsonList&lt;FacebookWork&gt;), friends (JsonList&lt;FacebookObject&gt;),
 * movies (JsonList&lt;FacebookInfo&gt;), music (JsonList&lt;FacebookInfo&gt;), books (JsonList&lt;FacebookInfo&gt;) and likes
 * (JsonList&lt;FacebookInfo&gt;).
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 * @see http://developers.facebook.com/docs/reference/api/user/
 */
public class FacebookProvider extends BaseOAuth20Provider {
    
    public final static String TYPE = FacebookProvider.class.getSimpleName();
    
    protected final static String BASE_URL = "https://graph.facebook.com/me";
    
    protected boolean friendsReturned = false;
    
    protected boolean moviesReturned = false;
    
    protected boolean musicReturned = false;
    
    protected boolean booksReturned = false;
    
    protected boolean likesReturned = false;
    
    @Override
    protected void internalInit() {
        if (StringHelper.isNotBlank(scope)) {
            service = new ServiceBuilder().provider(FacebookApi.class).apiKey(key).apiSecret(secret)
                .callback(callbackUrl).scope(scope).build();
        } else {
            service = new ServiceBuilder().provider(FacebookApi.class).apiKey(key).apiSecret(secret)
                .callback(callbackUrl).build();
        }
    }
    
    @Override
    protected String getProfileUrl() {
        return BASE_URL;
    }
    
    @Override
    public UserProfile getUserProfile(Token accessToken) {
        String body = sendRequestForData(accessToken, getProfileUrl());
        if (body == null) {
            return null;
        }
        FacebookProfile profile = (FacebookProfile) extractUserProfile(body);
        getData(friendsReturned, accessToken, profile, FacebookAttributesDefinition.FRIENDS, BASE_URL + "/friends");
        getData(moviesReturned, accessToken, profile, FacebookAttributesDefinition.MOVIES, BASE_URL + "/movies");
        getData(musicReturned, accessToken, profile, FacebookAttributesDefinition.MUSIC, BASE_URL + "/music");
        getData(booksReturned, accessToken, profile, FacebookAttributesDefinition.BOOKS, BASE_URL + "/books");
        getData(likesReturned, accessToken, profile, FacebookAttributesDefinition.LIKES, BASE_URL + "/likes");
        return profile;
    }
    
    private void getData(boolean dataReturned, Token accessToken, FacebookProfile profile, String attribute, String url) {
        if (dataReturned) {
            String body = sendRequestForData(accessToken, url);
            if (body != null) {
                JsonNode json = JsonHelper.getFirstNode(body);
                if (json != null) {
                    profile.addAttribute(attribute, JsonHelper.get(json, "data"));
                }
            }
        }
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        FacebookProfile profile = new FacebookProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (String attribute : AttributesDefinitions.facebookDefinition.getAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
        }
        return profile;
    }
    
    public boolean isFriendsReturned() {
        return friendsReturned;
    }
    
    public void setFriendsReturned(boolean friendsReturned) {
        this.friendsReturned = friendsReturned;
    }
    
    public boolean isMoviesReturned() {
        return moviesReturned;
    }
    
    public void setMoviesReturned(boolean moviesReturned) {
        this.moviesReturned = moviesReturned;
    }
    
    public boolean isMusicReturned() {
        return musicReturned;
    }
    
    public void setMusicReturned(boolean musicReturned) {
        this.musicReturned = musicReturned;
    }
    
    public boolean isBooksReturned() {
        return booksReturned;
    }
    
    public void setBooksReturned(boolean booksReturned) {
        this.booksReturned = booksReturned;
    }
    
    public boolean isLikesReturned() {
        return likesReturned;
    }
    
    public void setLikesReturned(boolean likesReturned) {
        this.likesReturned = likesReturned;
    }
}
