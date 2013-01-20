/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.oauth.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.ClientException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.credentials.OAuthCredentials;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.scribe.builder.api.ExtendedFacebookApi;
import org.scribe.model.OAuthConfig;
import org.scribe.model.SignatureType;
import org.scribe.oauth.FacebookOAuth20ServiceImpl;
import org.scribe.utils.OAuthEncoder;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class is the OAuth client to authenticate users in Facebook.
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
 * It returns a {@link org.pac4j.oauth.profile.facebook.FacebookProfile}.
 * <p />
 * More information at http://developers.facebook.com/docs/reference/api/user/
 * 
 * @see org.pac4j.oauth.profile.facebook.FacebookProfile
 * @author Jerome Leleu
 * @author Mehdi BEN HAJ ABBES
 * @since 1.0.0
 */
public class FacebookClient extends BaseOAuth20Client<FacebookProfile> {
    
    public final static String DEFAULT_FIELDS = "id,name,first_name,middle_name,last_name,gender,locale,languages,link,username,third_party_id,timezone,updated_time,verified,bio,birthday,education,email,hometown,interested_in,location,political,favorite_athletes,favorite_teams,quotes,relationship_status,religion,significant_other,website,work";
    
    protected String fields = DEFAULT_FIELDS;
    
    protected final static String BASE_URL = "https://graph.facebook.com/me";
    
    // Used as UserSession attribute and request parameter attribute for the the returned callbackUrl
    protected static final String FACEBOOK_STATE = "state";
    
    protected static final int RANDOM_STRING_LENGTH_10 = 10;
    
    public final static String DEFAULT_SCOPE = "user_likes,user_about_me,user_birthday,user_education_history,email,user_hometown,user_relationship_details,user_location,user_religion_politics,user_relationships,user_website,user_work_history";
    
    protected String scope = DEFAULT_SCOPE;
    
    public final static int DEFAULT_LIMIT = 0;
    
    protected int limit = DEFAULT_LIMIT;
    
    public FacebookClient() {
    }
    
    public FacebookClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }
    
    @Override
    protected FacebookClient newClient() {
        final FacebookClient newClient = new FacebookClient();
        newClient.setScope(this.scope);
        newClient.setFields(this.fields);
        newClient.setLimit(this.limit);
        return newClient;
    }
    
    @Override
    protected void internalInit() throws ClientException {
        super.internalInit();
        CommonHelper.assertNotBlank("fields", this.fields);
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
    protected FacebookProfile extractUserProfile(final String body) {
        final FacebookProfile profile = new FacebookProfile();
        final JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.get(json, "id"));
            for (final String attribute : OAuthAttributesDefinitions.facebookDefinition.getAllAttributes()) {
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
    
    /**
     * Retrieve the redirection url.
     * 
     * @param context
     * @return the redirection url
     */
    @Override
    public String retrieveRedirectionUrl(final WebContext context) {
        // Generating a random Facebook state parameter and storing it in the UserSession under 'state' attribute
        final String randomFacebookState = RandomStringUtils.randomAlphanumeric(RANDOM_STRING_LENGTH_10);
        logger.debug("Facebook state parameter: [{}]", randomFacebookState);
        context.setSessionAttribute(FACEBOOK_STATE, randomFacebookState);
        final String authorizationUrl = ((FacebookOAuth20ServiceImpl) this.service)
            .getAuthorizationUrl(randomFacebookState);
        logger.debug("authorizationUrl : {}", authorizationUrl);
        return authorizationUrl;
    }
    
    /**
     * Get the OAuth credentials from the web context.
     * 
     * @param context
     * @return the OAuth credentials
     * @throws OAuthCredentialsException
     */
    @Override
    protected OAuthCredentials getOAuthCredentials(final WebContext context) throws OAuthCredentialsException {
        // getting the Facebook state parameter from the callbackUrl returned by Facebook after authentication
        final String userSessionFacebookState = (String) context.getSessionAttribute(FACEBOOK_STATE);
        String stateVerifier = context.getRequestParameter(FACEBOOK_STATE);
        if (stateVerifier != null) {
            stateVerifier = OAuthEncoder.decode(stateVerifier);
            logger.debug("stateVerifier : {}", stateVerifier);
            if (stateVerifier.equals(userSessionFacebookState)) {
                return super.getOAuthCredentials(context);
            }
        }
        final String message = "Missing state parameter : session expired or possible threat of cross-site request forgery";
        logger.error(message);
        throw new OAuthCredentialsException(message);
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
