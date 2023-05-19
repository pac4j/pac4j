package org.pac4j.oauth.profile.generic;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.converter.StringConverter;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * <p>This class is the user profile for generic OAuth2 with appropriate getters.</p>
 * <p>The map of <code>profileAttributes</code> is intended to replace the primary/secondary attributes where
 * the key is the name of the attribute and the value is the path to obtain that attribute from the
 * json response starting from <code>firstNodePath</code></p>
 *
 * @author Julio Arrebola
 */
public class GenericOAuth20ProfileDefinition extends OAuthProfileDefinition {

    private final Map<String,String> profileAttributes = new HashMap<>();

    private String profileUrl = null;
    private Verb profileVerb = null;
    private String firstNodePath = null;

    /**
     * <p>Setter for the field <code>profileVerb</code>.</p>
     *
     * @param value a {@link Verb} object
     */
    public void setProfileVerb(final Verb value) {
        this.profileVerb = value;
    }

    /** {@inheritDoc} */
    @Override
    public Verb getProfileVerb() {
        if (profileVerb != null) {
            return this.profileVerb;
        } else {
            return super.getProfileVerb();
        }
    }

    /**
     * <p>Setter for the field <code>profileUrl</code>.</p>
     *
     * @param profileUrl a {@link String} object
     */
    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return profileUrl;
    }

    /** {@inheritDoc} */
    @Override
    public OAuth20Profile extractUserProfile(final String body) {
        val profile = new OAuth20Profile();
        val json = JsonHelper.getFirstNode(body, getFirstNodePath());
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, getProfileId())));
            for (val attribute : getPrimaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            for (val attribute : getSecondaryAttributes()) {
                convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
            }
            for (val entry : getProfileAttributes().entrySet()) {
                val key = entry.getKey();
                val value = entry.getValue();
                convertAndAdd(profile, PROFILE_ATTRIBUTE, key, JsonHelper.getElement(json, value));
            }

        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }

    /**
     * <p>Getter for the field <code>profileAttributes</code>.</p>
     *
     * @return a {@link Map} object
     */
    public Map<String, String> getProfileAttributes() {
        return this.profileAttributes;
    }

    /**
     * Add an attribute as a primary one and its converter.
     *
     * @param name name of the attribute
     * @param converter converter
     */
    public void profileAttribute(final String name, final AttributeConverter converter) {
        profileAttribute(name, name, converter);
    }

    /**
     * Add an attribute as a primary one and its converter.
     *
     * @param name name of the attribute
     * @param tag json reference
     * @param converter converter
     */
    public void profileAttribute(final String name, String tag, final AttributeConverter converter) {
        profileAttributes.put(name, tag);
        getConverters().put(name, Objects.requireNonNullElseGet(converter, StringConverter::new));
    }

    /**
     * <p>Getter for the field <code>firstNodePath</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getFirstNodePath() {
        return firstNodePath;
    }

    /**
     * <p>Setter for the field <code>firstNodePath</code>.</p>
     *
     * @param firstNodePath a {@link String} object
     */
    public void setFirstNodePath(final String firstNodePath) {
        this.firstNodePath = firstNodePath;
    }
}
