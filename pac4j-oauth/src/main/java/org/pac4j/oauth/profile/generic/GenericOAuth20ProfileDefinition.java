package org.pac4j.oauth.profile.generic;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.converter.StringConverter;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

/**
 * <p>This class is the user profile for generic OAuth2 with appropriate getters.</p>
 * <p>The map of <code>profileAttributes</code> is intended to replate the primary/secondary attributes where
 * the key is the name of the attribute and the value is the path to obtain that attribute from the
 * json resopnse, starting from <code>firstNodePath</code></p>
 *
 * @author Julio Arrebola
 * @author Vassilis Virvilis
 */
public class GenericOAuth20ProfileDefinition<P extends OAuth20Profile, C extends OAuth20Configuration>
        extends OAuth20ProfileDefinition<P, C> {

    private final Map<String, String> profileAttributes = new HashMap<>();

    String profileUrl = null;
    Verb profileVerb = null;
    String firstNodePath = null;

    public GenericOAuth20ProfileDefinition() {
        super();
    }

    public GenericOAuth20ProfileDefinition(final Function<Object[], P> profileFactory) {
        super(profileFactory);
    }

    public void setProfileVerb(final Verb value) {
        this.profileVerb = value;
    }

    @Override
    public Verb getProfileVerb() {
        if (profileVerb != null) {
            return this.profileVerb;
        }
        return super.getProfileVerb();
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return profileUrl;
    }

    @Override
    public P extractUserProfile(final String body) {
        final P profile = newProfile();
        final JsonNode json = JsonHelper.getFirstNode(body, getFirstNodePath());
        if (json != null) {
            if (getProfileId() != null) {
                profile.setId(ProfileHelper.sanitizeIdentifier(profile, JsonHelper.getElement(json, getProfileId())));
            }
            for (final String attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            for (final String attribute : getSecondaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            for (final Map.Entry<String, String> entry : getProfileAttributes().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                convertAndAdd(profile, PROFILE_ATTRIBUTE, key, JsonHelper.getElement(json, value));
            }

        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }

    public Map<String, String> getProfileAttributes() {
        return this.profileAttributes;
    }

     /**
     * Add an attribute as a primary one and its converter.
     *
     * @param name name of the attribute
     * @param converter converter
     */
    public void profileAttribute(final String name, final AttributeConverter<? extends Object> converter) {
        profileAttribute(name, name, converter);
    }

     /**
     * Add an attribute as a primary one and its converter.
     *
     * @param name name of the attribute
     * @param tag json reference
     * @param converter converter
     */
    public void profileAttribute(final String name, String tag, final AttributeConverter<? extends Object> converter) {
        profileAttributes.put(name, tag);
        if (converter != null) {
            getConverters().put(name, converter);
        } else {
            getConverters().put(name, new StringConverter());
        }
    }

    public String getFirstNodePath() {
        return firstNodePath;
    }

    public void setFirstNodePath(final String firstNodePath) {
        this.firstNodePath = firstNodePath;
    }
}
