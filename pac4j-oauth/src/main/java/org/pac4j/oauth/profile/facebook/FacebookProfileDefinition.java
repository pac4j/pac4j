package org.pac4j.oauth.profile.facebook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.apache.commons.codec.binary.Hex;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.converter.DateConverter;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.converter.JsonConverter;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * This class is the Facebook profile definition.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FacebookProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>NAME="name"</code> */
    public static final String NAME = "name";
    /** Constant <code>MIDDLE_NAME="middle_name"</code> */
    public static final String MIDDLE_NAME = "middle_name";
    /** Constant <code>LAST_NAME="last_name"</code> */
    public static final String LAST_NAME = "last_name";
    /** Constant <code>LANGUAGES="languages"</code> */
    public static final String LANGUAGES = "languages";
    /** Constant <code>LINK="link"</code> */
    public static final String LINK = "link";
    /** Constant <code>THIRD_PARTY_ID="third_party_id"</code> */
    public static final String THIRD_PARTY_ID = "third_party_id";
    /** Constant <code>TIMEZONE="timezone"</code> */
    public static final String TIMEZONE = "timezone";
    /** Constant <code>UPDATED_TIME="updated_time"</code> */
    public static final String UPDATED_TIME = "updated_time";
    /** Constant <code>VERIFIED="verified"</code> */
    public static final String VERIFIED = "verified";
    /** Constant <code>ABOUT="about"</code> */
    public static final String ABOUT = "about";
    /** Constant <code>BIRTHDAY="birthday"</code> */
    public static final String BIRTHDAY = "birthday";
    /** Constant <code>EDUCATION="education"</code> */
    public static final String EDUCATION = "education";
    /** Constant <code>HOMETOWN="hometown"</code> */
    public static final String HOMETOWN = "hometown";
    /** Constant <code>INTERESTED_IN="interested_in"</code> */
    public static final String INTERESTED_IN = "interested_in";
    /** Constant <code>POLITICAL="political"</code> */
    public static final String POLITICAL = "political";
    /** Constant <code>FAVORITE_ATHLETES="favorite_athletes"</code> */
    public static final String FAVORITE_ATHLETES = "favorite_athletes";
    /** Constant <code>FAVORITE_TEAMS="favorite_teams"</code> */
    public static final String FAVORITE_TEAMS = "favorite_teams";
    /** Constant <code>QUOTES="quotes"</code> */
    public static final String QUOTES = "quotes";
    /** Constant <code>RELATIONSHIP_STATUS="relationship_status"</code> */
    public static final String RELATIONSHIP_STATUS = "relationship_status";
    /** Constant <code>RELIGION="religion"</code> */
    public static final String RELIGION = "religion";
    /** Constant <code>SIGNIFICANT_OTHER="significant_other"</code> */
    public static final String SIGNIFICANT_OTHER = "significant_other";
    /** Constant <code>WEBSITE="website"</code> */
    public static final String WEBSITE = "website";
    /** Constant <code>WORK="work"</code> */
    public static final String WORK = "work";
    /** Constant <code>FRIENDS="friends"</code> */
    public static final String FRIENDS = "friends";
    /** Constant <code>MOVIES="movies"</code> */
    public static final String MOVIES = "movies";
    /** Constant <code>MUSIC="music"</code> */
    public static final String MUSIC = "music";
    /** Constant <code>BOOKS="books"</code> */
    public static final String BOOKS = "books";
    /** Constant <code>LIKES="likes"</code> */
    public static final String LIKES = "likes";
    /** Constant <code>ALBUMS="albums"</code> */
    public static final String ALBUMS = "albums";
    /** Constant <code>EVENTS="events"</code> */
    public static final String EVENTS = "events";
    /** Constant <code>GROUPS="groups"</code> */
    public static final String GROUPS = "groups";
    /** Constant <code>MUSIC_LISTENS="music.listens"</code> */
    public static final String MUSIC_LISTENS = "music.listens";
    /** Constant <code>PICTURE="picture"</code> */
    public static final String PICTURE = "picture";

    /** Constant <code>DEFAULT_LIMIT=0</code> */
    public static final int DEFAULT_LIMIT = 0;

    /** Constant <code>BASE_URL="https://graph.facebook.com/v2.8/me"</code> */
    protected static final String BASE_URL = "https://graph.facebook.com/v2.8/me";

    /** Constant <code>APPSECRET_PARAMETER="appsecret_proof"</code> */
    protected static final String APPSECRET_PARAMETER = "appsecret_proof";

    /**
     * <p>Constructor for FacebookProfileDefinition.</p>
     */
    public FacebookProfileDefinition() {
        super(x -> new FacebookProfile());
        Arrays.stream(new String[] {
            NAME, MIDDLE_NAME, LAST_NAME, THIRD_PARTY_ID, ABOUT, POLITICAL, QUOTES, RELIGION, WEBSITE
        }).forEach(a -> primary(a, Converters.STRING));
        primary(TIMEZONE, Converters.INTEGER);
        primary(VERIFIED, Converters.BOOLEAN);
        primary(LINK, Converters.URL);
        AttributeConverter objectConverter = new JsonConverter(FacebookObject.class);
        AttributeConverter multiObjectConverter = new JsonConverter(List.class, new TypeReference<List<FacebookObject>>() {});
        AttributeConverter multiInfoConverter = new JsonConverter(List.class, new TypeReference<List<FacebookInfo>>() {});
        primary(UPDATED_TIME, Converters.DATE_TZ_GENERAL);
        primary(BIRTHDAY, new DateConverter("MM/dd/yyyy"));
        primary(RELATIONSHIP_STATUS, new FacebookRelationshipStatusConverter());
        primary(LANGUAGES, multiObjectConverter);
        primary(EDUCATION, new JsonConverter(List.class, new TypeReference<List<FacebookEducation>>() {}));
        primary(HOMETOWN, objectConverter);
        primary(INTERESTED_IN, new JsonConverter(List.class, new TypeReference<List<String>>() {}));
        primary(LOCATION, objectConverter);
        primary(FAVORITE_ATHLETES, multiObjectConverter);
        primary(FAVORITE_TEAMS, multiObjectConverter);
        primary(SIGNIFICANT_OTHER, objectConverter);
        primary(WORK, new JsonConverter(List.class, new TypeReference<List<FacebookWork>>() {}));
        secondary(FRIENDS, multiObjectConverter);
        secondary(MOVIES, multiInfoConverter);
        secondary(MUSIC, multiInfoConverter);
        secondary(BOOKS, multiInfoConverter);
        secondary(LIKES, multiInfoConverter);
        secondary(ALBUMS, new JsonConverter(List.class, new TypeReference<List<FacebookPhoto>>() {}));
        secondary(EVENTS, new JsonConverter(List.class, new TypeReference<List<FacebookEvent>>() {}));
        secondary(GROUPS, new JsonConverter(List.class, new TypeReference<List<FacebookGroup>>() {}));
        secondary(MUSIC_LISTENS, new JsonConverter(List.class, new TypeReference<List<FacebookMusicListen>>() {}));
        secondary(PICTURE, new JsonConverter(FacebookPicture.class));
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        val fbConfiguration = (FacebookConfiguration) configuration;
        var url = BASE_URL + "?fields=" + fbConfiguration.getFields();
        if (fbConfiguration.getLimit() > DEFAULT_LIMIT) {
            url += "&limit=" + fbConfiguration.getLimit();
        }
        // possibly include the appsecret_proof parameter
        if (fbConfiguration.isUseAppsecretProof()) {
            url = computeAppSecretProof(url, (OAuth2AccessToken) accessToken, fbConfiguration);
        }
        return url;
    }

    /**
     * The code in this method is based on this blog post:
     * https://www.sammyk.me/the-single-most-important-way-to-make-your-facebook-app-more-secure
     * and this answer: https://stackoverflow.com/questions/7124735/hmac-sha256-algorithm-for-signature-calculation
     *
     * @param url the URL to which we're adding the proof
     * @param token the application token we pass back and forth
     * @param configuration the current configuration
     * @return URL with the appsecret_proof parameter added
     */
    public String computeAppSecretProof(final String url, final OAuth2AccessToken token, final FacebookConfiguration configuration) {
        try {
            var sha256_HMAC = Mac.getInstance("HmacSHA256");
            Key secret_key = new SecretKeySpec(configuration.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            var proof = Hex.encodeHexString(sha256_HMAC.doFinal(token.getAccessToken()
                .getBytes(StandardCharsets.UTF_8)));
            val computedUrl = CommonHelper.addParameter(url, APPSECRET_PARAMETER, proof);
            return computedUrl;
        } catch (final InvalidKeyException | NoSuchAlgorithmException e) {
            throw new TechnicalException("Unable to compute appsecret_proof", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public FacebookProfile extractUserProfile(final String body) {
        val profile = (FacebookProfile) newProfile();
        val json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "id")));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            extractData(profile, json, FacebookProfileDefinition.FRIENDS);
            extractData(profile, json, FacebookProfileDefinition.MOVIES);
            extractData(profile, json, FacebookProfileDefinition.MUSIC);
            extractData(profile, json, FacebookProfileDefinition.BOOKS);
            extractData(profile, json, FacebookProfileDefinition.LIKES);
            extractData(profile, json, FacebookProfileDefinition.ALBUMS);
            extractData(profile, json, FacebookProfileDefinition.EVENTS);
            extractData(profile, json, FacebookProfileDefinition.GROUPS);
            extractData(profile, json, FacebookProfileDefinition.MUSIC_LISTENS);
            extractData(profile, json, FacebookProfileDefinition.PICTURE);
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }

    /**
     * <p>extractData.</p>
     *
     * @param profile a {@link FacebookProfile} object
     * @param json a {@link JsonNode} object
     * @param name a {@link String} object
     */
    protected void extractData(final UserProfile profile, final JsonNode json, final String name) {
        val data = (JsonNode) JsonHelper.getElement(json, name);
        if (data != null) {
            convertAndAdd(profile, PROFILE_ATTRIBUTE, name, JsonHelper.getElement(data, "data"));
        }
    }
}
