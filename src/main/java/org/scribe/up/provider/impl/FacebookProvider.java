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

import org.codehaus.jackson.JsonNode;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
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
import org.scribe.up.util.StringHelper;
import org.scribe.utils.OAuthEncoder;

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
 * (JsonList&lt;FacebookInfo&gt;), albums (JsonList&lt;FacebookPhoto&gt;), events (JsonList&lt;FacebookEvent&gt;) and groups
 * (JsonList&lt;FacebookGroup&gt;).<br />
 * More information at http://developers.facebook.com/docs/reference/api/user/
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookProvider extends BaseOAuth20Provider {
    
    protected static final String FACEBOOK_STATE = "state";
    
    public final static String DEFAULT_FIELDS = "id,name,first_name,middle_name,last_name,gender,locale,languages,link,username,third_party_id,timezone,updated_time,verified,bio,birthday,education,email,hometown,interested_in,location,political,favorite_athletes,favorite_teams,quotes,relationship_status,religion,significant_other,website,work";
    
    protected final static String BASE_URL = "https://graph.facebook.com/me";
    
    protected String fields = DEFAULT_FIELDS;
    
    protected String scope;
    
    protected String state;
    
    public final static int DEFAULT_LIMIT = 0;
    
    protected int limit = DEFAULT_LIMIT;
    
    @Override
    protected FacebookProvider newProvider() {
        final FacebookProvider newProvider = new FacebookProvider();
        newProvider.setScope(this.scope);
        newProvider.setFields(this.fields);
        newProvider.setLimit(this.limit);
        newProvider.setState(this.state);
        return newProvider;
    }
    
    @Override
    protected void internalInit() {
        if (StringHelper.isNotBlank(this.state)) {
            if (StringHelper.isNotBlank(this.scope)) {
                this.service = new ServiceBuilder().provider(ExtendedFacebookApi.class).apiKey(this.key)
                    .apiSecret(this.secret).callback(this.callbackUrl).scope(this.scope).build();
            } else {
                this.service = new ServiceBuilder().provider(ExtendedFacebookApi.class).apiKey(this.key)
                    .apiSecret(this.secret).callback(this.callbackUrl).build();
            }
            
        } else {
            if (StringHelper.isNotBlank(this.scope)) {
                this.service = new ServiceBuilder().provider(FacebookApi.class).apiKey(this.key).apiSecret(this.secret)
                    .callback(this.callbackUrl).scope(this.scope).build();
            } else {
                this.service = new ServiceBuilder().provider(FacebookApi.class).apiKey(this.key).apiSecret(this.secret)
                    .callback(this.callbackUrl).build();
            }
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
            this.extractData(profile, json, FacebookAttributesDefinition.FRIENDS);
            this.extractData(profile, json, FacebookAttributesDefinition.MOVIES);
            this.extractData(profile, json, FacebookAttributesDefinition.MUSIC);
            this.extractData(profile, json, FacebookAttributesDefinition.BOOKS);
            this.extractData(profile, json, FacebookAttributesDefinition.LIKES);
            this.extractData(profile, json, FacebookAttributesDefinition.ALBUMS);
            this.extractData(profile, json, FacebookAttributesDefinition.EVENTS);
            this.extractData(profile, json, FacebookAttributesDefinition.GROUPS);
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
        if (StringHelper.isNotBlank(this.state)) {
            this.init();
            authorizationUrl = ((FacebookOAuth20ServiceImpl) this.service).getAuthorizationUrl(this.state);
        } else {
            final String userSessionFacebookState = (String) session.getAttribute(FACEBOOK_STATE);
            if (StringHelper.isNotBlank(userSessionFacebookState)) {
                this.state = userSessionFacebookState;
                this.init();
                authorizationUrl = ((FacebookOAuth20ServiceImpl) this.service).getAuthorizationUrl(this.state);
            } else {
                this.init();
                // no requestToken for OAuth 2.0 -> no need to save it in the user session
                authorizationUrl = this.service.getAuthorizationUrl(null);
            }
        }
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    @Override
    public OAuthCredential extractCredentialFromParameters(final UserSession session,
                                                           final Map<String, String[]> parameters) {
        final String[] stateVerifiers = parameters.get(FACEBOOK_STATE);
        if (stateVerifiers != null && stateVerifiers.length == 1) {
            final String stateVerifier = OAuthEncoder.decode(stateVerifiers[0]);
            logger.debug("stateVerifier : {}", stateVerifier);
            if (stateVerifier.equals(this.state)) {
                return super.extractCredentialFromParameters(session, parameters);
            } else {
                logger.error("Possible threat of Cross-site Request Forgery.");
                return null;
            }
        } else {
            return super.extractCredentialFromParameters(session, parameters);
        }
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
    
    public String getState() {
        return this.state;
    }
    
    public void setState(final String state) {
        this.state = state;
    }
    
}
