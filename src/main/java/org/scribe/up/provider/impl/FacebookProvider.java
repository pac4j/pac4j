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

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.up.addon_to_scribe.ExtendedFacebookApi;
import org.scribe.up.addon_to_scribe.FacebookOAuth20ServiceImpl;
import org.scribe.up.credential.OAuthCredential;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.facebook.FacebookAttributesDefinition;
import org.scribe.up.profile.facebook.FacebookProfile;
import org.scribe.up.provider.BaseOAuth20Provider;
import org.scribe.up.session.UserSession;
import org.scribe.utils.OAuthEncoder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth provider to authenticate user in Facebook. Specific scopes and fields can be requested to get more attributes and
 * the number of results can be limited.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.facebook.FacebookProfile} : name (String), first_name (String),
 * middle_name (String), last_name (String), gender (Gender), locale (Locale), languages (JsonList&lt;FacebookObject&gt;), link (String),
 * username (String), third_party_id (String), timezone (Integer), updated_time (FormattedDate), verified (Boolean), bio (String), birthday
 * (FormattedDate), education (JsonList&lt;FacebookEducation&gt;), email (String), hometown (FacebookObject), interested_in
 * (JsonList&lt;String&gt;), location (FacebookObject), political (String), favorite_athletes (JsonList&lt;FacebookObject&gt;),
 * favorite_teams (JsonList&lt;FacebookObject&gt;), quotes (String), relationship_status (FacebookRelationshipStatus), religion (String),
 * significant_other (FacebookObject), website (String), work (JsonList&lt;FacebookWork&gt;), friends (JsonList&lt;FacebookObject&gt;),
 * movies (JsonList&lt;FacebookInfo&gt;), music (JsonList&lt;FacebookInfo&gt;), books (JsonList&lt;FacebookInfo&gt;), likes
 * (JsonList&lt;FacebookInfo&gt;), albums (JsonList&lt;FacebookPhoto&gt;), events (JsonList&lt;FacebookEvent&gt;), groups
 * (JsonList&lt;FacebookGroup&gt;), music.listens (JsonList&lt;FacebookMusicListenGroup&gt;) and picture (FacebookPicture).<br />
 * More information at http://developers.facebook.com/docs/reference/api/user/
 * 
 * @author Jerome Leleu
 * @author Mehdi BEN HAJ ABBES
 * @since 1.0.0
 */
public class FacebookProvider extends BaseOAuth20Provider {
    
    public final static String DEFAULT_FIELDS = "id,name,first_name,middle_name,last_name,gender,locale,languages,link,username,third_party_id,timezone,updated_time,verified,bio,birthday,education,email,hometown,interested_in,location,political,favorite_athletes,favorite_teams,quotes,relationship_status,religion,significant_other,website,work";
    
    protected final static String BASE_URL = "https://graph.facebook.com/me";
    
    // Used as UserSession attribute and request parameter attribute for the the returned callbackUrl
    protected static final String FACEBOOK_STATE = "state";
    
    protected static final int RANDOM_STRING_LENGTH_10 = 10;
    
    protected String fields = DEFAULT_FIELDS;
    
    protected String scope;
    
    public final static int DEFAULT_LIMIT = 0;
    
    protected int limit = DEFAULT_LIMIT;
    
    @Override
    protected FacebookProvider newProvider() {
        final FacebookProvider newProvider = new FacebookProvider();
        newProvider.setScope(this.scope);
        newProvider.setFields(this.fields);
        newProvider.setLimit(this.limit);
        return newProvider;
    }
    
    @Override
    protected void internalInit() {
        if (StringUtils.isNotBlank(this.scope)) {
            this.service = new FacebookOAuth20ServiceImpl(new ExtendedFacebookApi(),
                                                          new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                          SignatureType.Header, this.scope, null),
                                                          this.proxyHost, this.proxyPort);
        } else {
            this.service = new FacebookOAuth20ServiceImpl(new ExtendedFacebookApi(),
                                                          new OAuthConfig(this.key, this.secret, this.callbackUrl,
                                                                          SignatureType.Header, null, null),
                                                          this.proxyHost, this.proxyPort);
        }
    }
    
    @Override
    protected String getProfileUrl() {
        String url = BASE_URL + "?fields=" + this.fields;
        if (this.limit > DEFAULT_LIMIT) {
            url += "&limit=" + this.limit;
        }
        return url;
    }
    
    @Override
    protected UserProfile extractUserProfile(final String body) {
        final FacebookProfile profile = new FacebookProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : AttributesDefinitions.facebookDefinition.getAllAttributes()) {
                profile.addAttribute(attribute, JsonHelper.get(json, attribute));
            }
            extractData(profile, json, FacebookAttributesDefinition.FRIENDS);
            extractData(profile, json, FacebookAttributesDefinition.MOVIES);
            extractData(profile, json, FacebookAttributesDefinition.MUSIC);
            extractData(profile, json, FacebookAttributesDefinition.BOOKS);
            extractData(profile, json, FacebookAttributesDefinition.LIKES);
            extractData(profile, json, FacebookAttributesDefinition.ALBUMS);
            extractData(profile, json, FacebookAttributesDefinition.EVENTS);
            extractData(profile, json, FacebookAttributesDefinition.GROUPS);
            extractData(profile, json, FacebookAttributesDefinition.MUSIC_LISTENS);
            extractData(profile, json, FacebookAttributesDefinition.PICTURE);
        }
        return profile;
    }
    
    protected void extractData(final FacebookProfile profile, final JsonNode json, final String name) {
        final JsonNode data = (JsonNode) JsonHelper.get(json, name);
        if (data != null) {
            profile.addAttribute(name, JsonHelper.get(data, "data"));
        }
    }
    
    @Override
    public String getAuthorizationUrl(final UserSession session) {
        String authorizationUrl = null;
        // Generating a random Facebook state parameter and storing it in the UserSession under 'state' attribute
        final String randomFacebookState = RandomStringUtils.randomAlphanumeric(RANDOM_STRING_LENGTH_10);
        logger.debug("Facebook state parameter: [{}]", randomFacebookState);
        session.setAttribute(FACEBOOK_STATE, randomFacebookState);
        init();
        authorizationUrl = ((FacebookOAuth20ServiceImpl) this.service).getAuthorizationUrl(randomFacebookState);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    @Override
    protected OAuthCredential extractCredentialFromParameters(final UserSession session,
                                                              final Map<String, String[]> parameters) {
        // getting the Facebook state parameter from the callbackUrl returned by Facebook after authentication
        final String userSessionFacebookState = (String) session.getAttribute(FACEBOOK_STATE);
        final String[] stateVerifiers = parameters.get(FACEBOOK_STATE);
        if (stateVerifiers != null && stateVerifiers.length == 1) {
            final String stateVerifier = OAuthEncoder.decode(stateVerifiers[0]);
            logger.debug("stateVerifier : {}", stateVerifier);
            if (stateVerifier.equals(userSessionFacebookState)) {
                return super.extractCredentialFromParameters(session, parameters);
            }
        }
        logger.error("Possible threat of Cross-site Request Forgery.");
        return null;
    }
    
    public String getScope() {
        return this.scope;
    }
    
    public void setScope(final String scope) {
        this.scope = scope;
    }
    
    public String getFields() {
        return this.fields;
    }
    
    public void setFields(final String fields) {
        this.fields = fields;
    }
    
    public int getLimit() {
        return this.limit;
    }
    
    public void setLimit(final int limit) {
        this.limit = limit;
    }
}
