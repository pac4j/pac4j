package org.pac4j.core.profile.definition;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.core.util.Pac4jConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.profile.AttributeLocation.AUTHENTICATION_ATTRIBUTE;
import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;
import static org.pac4j.core.util.CommonHelper.assertNotNull;
import static org.pac4j.core.util.CommonHelper.substringBefore;

/**
 * Define a profile (its class and attributes).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
public abstract class ProfileDefinition {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Setter
    private String profileId = null;

    private final List<String> primaryAttributes = new ArrayList<>();

    private final List<String> secondaryAttributes = new ArrayList<>();

    private final Map<String, AttributeConverter> converters = new HashMap<>();

    @Setter
    private ProfileFactory profileFactory = parameters -> new CommonProfile();

    @Setter
    private boolean restoreProfileFromTypedId = false;

    /**
     * Return the new built or restored profile.
     *
     * @param parameters some input parameters (the first optional one is the typed id)
     * @return the new built or restored profile
     */
    public UserProfile newProfile(final Object... parameters) {
        if (restoreProfileFromTypedId) {
            val typedId = getParameter(parameters, 0);
            if (typedId instanceof String) {
                logger.debug("Building user profile based on typedId: {}", typedId);
                val sTypedId = (String) typedId;
                if (sTypedId.contains(Pac4jConstants.TYPED_ID_SEPARATOR)) {
                    val profileClass = substringBefore(sTypedId, Pac4jConstants.TYPED_ID_SEPARATOR);
                    for (val profileClassPrefix : ProfileHelper.getProfileClassPrefixes()) {
                        if (profileClass.startsWith(profileClassPrefix)) {
                            try {
                                return ProfileHelper.buildUserProfileByClassCompleteName(profileClass);
                            } catch (final TechnicalException e) {
                                logger.error("Cannot build instance for class name: {}", profileClass, e);
                            }
                        }
                    }
                }
            }
        }
        return profileFactory.apply(parameters);
    }

    /**
     * <p>getParameter.</p>
     *
     * @param parameters an array of {@link java.lang.Object} objects
     * @param num a int
     * @return a {@link java.lang.Object} object
     */
    protected Object getParameter(final Object[] parameters, final int num) {
        if (parameters != null && parameters.length >= num) {
            return parameters[num];
        } else {
            return null;
        }
    }

    /**
     * Convert a profile or authentication attribute, if necessary, and add it to the profile.
     *
     * @param profile The profile.
     * @param attributeLocation Location of the attribute inside the profile: classic profile attribute, authentication attribute, ...
     * @param name The attribute name.
     * @param value The attribute value.
     */
    public void convertAndAdd(final UserProfile profile, final AttributeLocation attributeLocation, final String name,
            final Object value) {
        if (value != null) {
            final Object convertedValue;
            val converter = this.converters.get(name);
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
    public void convertAndAdd(final UserProfile profile,
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
    protected void setProfileFactory(final ProfileFactory profileFactory) {
        assertNotNull("profileFactory", profileFactory);
        this.profileFactory = profileFactory;
    }

    /**
     * Add an attribute as a primary one and its converter.
     *
     * @param name name of the attribute
     * @param converter converter
     */
    protected void primary(final String name, final AttributeConverter converter) {
        primaryAttributes.add(name);
        converters.put(name, converter);
    }

    /**
     * Add an attribute as a secondary one and its converter.
     *
     * @param name name of the attribute
     * @param converter converter
     */
    protected void secondary(final String name, final AttributeConverter converter) {
        secondaryAttributes.add(name);
        converters.put(name, converter);
    }
}
