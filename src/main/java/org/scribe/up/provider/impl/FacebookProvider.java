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
 * This class is the OAuth provider to authenticate user in Facebook.
 * <p />
 * By default, the following <i>scope</i> is requested to Facebook : user_likes, user_about_me, user_birthday, user_education_history,
 * email, user_hometown, user_relationship_details, user_location, user_religion_politics, user_relationships, user_website and
 * user_work_history.<br />
 * The <i>scope</i> can be defined to require permissions from the user and retrieve fields from Facebook, by using the
 * {@link #setScope(String)} method.<br />
 * By default, the following <i>fields</i> are requested to Facebook : id, name, first_name, middle_name, last_name, gender, locale,
 * languages, link, username, third_party_id, timezone, updated_time, verified, bio, birthday, education, email, hometown, interested_in,
 * location, political, favorite_athletes, favorite_teams, quotes, relationship_status, religion, significant_other, website and work.<br />
 * The <i>fields</i> can be defined and requested to Facebook, by using the {@link #setFields(String)} method.
 * <p />
 * The number of results can be limited by using the {@link #setLimit(int)} method.
 * <p />
 * It returns a {@link org.scribe.up.profile.facebook.FacebookProfile}.
 * <p />
 * More information at http://developers.facebook.com/docs/reference/api/user/
 * 
 * @see org.scribe.up.profile.facebook.FacebookProfile
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
    
    protected String scope = "user_likes,user_about_me,user_birthday,user_education_history,email,user_hometown,user_relationship_details,user_location,user_religion_politics,user_relationships,user_website,user_work_history";
    
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
