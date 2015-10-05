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
package org.pac4j.oauth.client;

import org.apache.commons.lang3.StringUtils;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.HttpCommunicationException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.client.exception.OAuthCredentialsException;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.facebook.FacebookAttributesDefinition;
import org.pac4j.oauth.profile.facebook.FacebookProfile;
import org.scribe.builder.api.ExtendedFacebookApi;
import org.scribe.builder.api.StateApi20;
import org.scribe.model.OAuthConfig;
import org.scribe.model.OAuthConstants;
import org.scribe.model.ProxyOAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.oauth.StateOAuth20ServiceImpl;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * <p>This class is the OAuth client to authenticate users in Facebook.</p>
 * <p>By default, the following <i>scope</i> is requested to Facebook : user_likes, user_about_me, user_birthday, user_education_history,
 * email, user_hometown, user_relationship_details, user_location, user_religion_politics, user_relationships, user_website and
 * user_work_history.</p>
 * <p>The <i>scope</i> can be defined to require permissions from the user and retrieve fields from Facebook, by using the
 * {@link #setScope(String)} method.</p>
 * <p>By default, the following <i>fields</i> are requested to Facebook : id, name, first_name, middle_name, last_name, gender, locale,
 * languages, link, username, third_party_id, timezone, updated_time, verified, bio, birthday, education, email, hometown, interested_in,
 * location, political, favorite_athletes, favorite_teams, quotes, relationship_status, religion, significant_other, website and work.</p>
 * <p>The <i>fields</i> can be defined and requested to Facebook, by using the {@link #setFields(String)} method.</p>
 * <p>The number of results can be limited by using the {@link #setLimit(int)} method.</p>
 * <p>An extended access token can be requested by setting <code>true</code> on the {@link #setRequiresExtendedToken(boolean)} method.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.facebook.FacebookProfile}.</p>
 * <p>More information at http://developers.facebook.com/docs/reference/api/user/</p>
 * <p>More information at https://developers.facebook.com/docs/howtos/login/extending-tokens/</p>
 * 
 * @see org.pac4j.oauth.profile.facebook.FacebookProfile
 * @author Jerome Leleu
 * @author Mehdi BEN HAJ ABBES
 * @since 1.0.0
 */
public class FacebookClient extends BaseOAuth20Client<FacebookProfile> {
    
    private static final String EXCHANGE_TOKEN_URL = "https://graph.facebook.com/v2.4/oauth/access_token?grant_type=fb_exchange_token";
    
    private static final String EXCHANGE_TOKEN_PARAMETER = "fb_exchange_token";
    
    public final static String DEFAULT_FIELDS = "id,name,first_name,middle_name,last_name,gender,locale,languages,link,third_party_id,timezone,updated_time,verified,bio,birthday,education,email,hometown,interested_in,location,political,favorite_athletes,favorite_teams,quotes,relationship_status,religion,significant_other,website,work";
    
    protected String fields = DEFAULT_FIELDS;
    
    protected final static String BASE_URL = "https://graph.facebook.com/v2.4/me";
    
    public final static String DEFAULT_SCOPE = "user_likes,user_about_me,user_birthday,user_education_history,email,user_hometown,user_relationship_details,user_location,user_religion_politics,user_relationships,user_website,user_work_history";
    
    protected String scope = DEFAULT_SCOPE;
    
    public final static int DEFAULT_LIMIT = 0;
    
    protected int limit = DEFAULT_LIMIT;
    
    protected boolean requiresExtendedToken = false;
    
    protected StateApi20 api20;
    
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
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("fields", this.fields);
        this.api20 = new ExtendedFacebookApi();
        if (StringUtils.isNotBlank(this.scope)) {
            this.service = new StateOAuth20ServiceImpl(this.api20, new OAuthConfig(this.key, this.secret,
                                                                                   this.callbackUrl,
                                                                                   SignatureType.Header, this.scope,
                                                                                   null), this.connectTimeout,
                                                       this.readTimeout, this.proxyHost, this.proxyPort);
        } else {
            this.service = new StateOAuth20ServiceImpl(this.api20, new OAuthConfig(this.key, this.secret,
                                                                                   this.callbackUrl,
                                                                                   SignatureType.Header, null, null),
                                                       this.connectTimeout, this.readTimeout, this.proxyHost,
                                                       this.proxyPort);
        }
    }
    
    @Override
    protected String getProfileUrl(final Token accessToken) {
        String url = BASE_URL + "?fields=" + this.fields;
        if (this.limit > DEFAULT_LIMIT) {
            url += "&limit=" + this.limit;
        }
        return url;
    }
    
    @Override
    protected FacebookProfile retrieveUserProfileFromToken(final Token accessToken) {
        String body = sendRequestForData(accessToken, getProfileUrl(accessToken));
        if (body == null) {
            throw new HttpCommunicationException("Not data found for accessToken : " + accessToken);
        }
        final FacebookProfile profile = extractUserProfile(body);
        addAccessTokenToProfile(profile, accessToken);
        if (profile != null && this.requiresExtendedToken) {
            String url = CommonHelper.addParameter(EXCHANGE_TOKEN_URL, OAuthConstants.CLIENT_ID, this.key);
            url = CommonHelper.addParameter(url, OAuthConstants.CLIENT_SECRET, this.secret);
            url = CommonHelper.addParameter(url, EXCHANGE_TOKEN_PARAMETER, accessToken.getToken());
            final ProxyOAuthRequest request = createProxyRequest(url);
            final long t0 = System.currentTimeMillis();
            final Response response = request.send();
            final int code = response.getCode();
            body = response.getBody();
            final long t1 = System.currentTimeMillis();
            logger.debug("Request took : " + (t1 - t0) + " ms for : " + url);
            logger.debug("response code : {} / response body : {}", code, body);
            if (code == 200) {
                logger.debug("Retrieve extended token from : {}", body);
                final Token extendedAccessToken = this.api20.getAccessTokenExtractor().extract(body);
                logger.debug("Extended token : {}", extendedAccessToken);
                addAccessTokenToProfile(profile, extendedAccessToken);
            } else {
                logger.error("Cannot get extended token : {} / {}", code, body);
            }
        }
        return profile;
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
    
    @Override
    protected boolean hasBeenCancelled(final WebContext context) {
        final String error = context.getRequestParameter(OAuthCredentialsException.ERROR);
        final String errorReason = context.getRequestParameter(OAuthCredentialsException.ERROR_REASON);
        // user has denied permissions
        if ("access_denied".equals(error) && "user_denied".equals(errorReason)) {
            return true;
        } else {
            return false;
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
    
    public boolean isRequiresExtendedToken() {
        return this.requiresExtendedToken;
    }
    
    public void setRequiresExtendedToken(final boolean requiresExtendedToken) {
        this.requiresExtendedToken = requiresExtendedToken;
    }
    
    @Override
    protected boolean requiresStateParameter() {
        return true;
    }
}
