package org.pac4j.core.profile.definition;

import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.pac4j.core.profile.AttributeLocation.AUTHENTICATION_ATTRIBUTE;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Define a profile (its class and attributes).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class ProfileDefinition<P extends CommonProfile> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String profileId = null;

    private final List<String> primaries = new ArrayList<>();

    private final List<String> secondaries = new ArrayList<>();

    private final Map<String, AttributeConverter<? extends Object>> converters = new HashMap<>();

    protected ProfileFactory<P> newProfile = parameters -> (P) new CommonProfile();

    /**
     * Return the new built profile.
     *
     * @param parameters some optional input parameters
     * @return the new built profile
     */
    public P newProfile(final Object... parameters) {
        return newProfile.apply(parameters);
    }

    /**
     * Convert a profile or authentication attribute, if necessary, and add it to the profile.
     *
     * @param profile The profile.
     * @param attributeLocation Location of the attribute inside the profile: classic profile attribute, authentication attribute, ...
     * @param name The attribute name.
     * @param value The attribute value.
     */
    public void convertAndAdd(final CommonProfile profile, final AttributeLocation attributeLocation, final String name,
            final Object value) {
        if (value != null) {
            final Object convertedValue;
            final AttributeConverter<? extends Object> converter = this.converters.get(name);
            if (converter != null) {
                convertedValue = converter.convert(value);
                if (convertedValue != null) {
                    logger.debug("converted to => key: {} / value: {} / {}", name, convertedValue, convertedValue.getClass());
                }
            } else {
                convertedValue = value;
                logger.debug("no conversion => key: {} / value: {} / {}", name, convertedValue, convertedValue.getClass());
            }

            if (attributeLocation.equals(AUTHENTICATION_ATTRIBUTE)) {
                profile.addAuthenticationAttribute(name, convertedValue);
            } else {
                profile.addAttribute(name, convertedValue);
            }
        }
    }

    /**
     * Convert the profile and authentication attributes, if necessary, and add them to the profile.
     *
     * @param profile The profile.
     * @param profileAttributes The profile attributes. May be {@code null}.
     * @param authenticationAttributes The authentication attributes. May be {@code null}.
     */
    public void convertAndAdd(final CommonProfile profile,
            final Map<String, Object> profileAttributes,
            final Map<String, Object> authenticationAttributes) {
        if (profileAttributes != null) {
            profileAttributes.entrySet().stream()
                .forEach(entry -> convertAndAdd(profile, PROFILE_ATTRIBUTE, entry.getKey(), entry.getValue()));
        }
        if (authenticationAttributes != null) {
            authenticationAttributes.entrySet().stream()
                .forEach(entry -> convertAndAdd(profile, AUTHENTICATION_ATTRIBUTE, entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Define the way to build the profile.
     *
     * @param profileFactory the way to build the profile
     */
    protected void setProfileFactory(final ProfileFactory<P> profileFactory) {
        CommonHelper.assertNotNull("profileFactory", profileFactory);
        this.newProfile = profileFactory;
    }

    /**
     * Add an attribute as a primary one and its converter.
     *
     * @param name name of the attribute
     * @param converter converter
     */
    protected void primary(final String name, final AttributeConverter<? extends Object> converter) {
        primaries.add(name);
        converters.put(name, converter);
    }

    /**
     * Add an attribute as a secondary one and its converter.
     *
     * @param name name of the attribute
     * @param converter converter
     */
    protected void secondary(final String name, final AttributeConverter<? extends Object> converter) {
        secondaries.add(name);
        converters.put(name, converter);
    }

    public List<String> getPrimaryAttributes() {
        return this.primaries;
    }

    public List<String> getSecondaryAttributes() {
        return this.secondaries;
    }

    protected Map<String, AttributeConverter<? extends Object>> getConverters() {
        return converters;
    }

    public void setProfileId(final String profileId) {
        this.profileId = profileId;
    }

    public String getProfileId() {
        return profileId;
    }
}
