package org.pac4j.core.profile.definition;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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

    protected Function<Object[], P> newProfile = parameters -> (P) new CommonProfile();

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
     * Convert the attribute if necessary and add it to the profile.
     *
     * @param profile the profile
     * @param name the attribute name
     * @param value the attribute value
     */
    public void convertAndAdd(final CommonProfile profile, final String name, final Object value) {
        if (value != null) {
            final Object convertedValue;
            final AttributeConverter<? extends Object> converter = this.converters.get(name);
            if (converter != null) {
                convertedValue = converter.convert(value);
                if (convertedValue != null) {
                    logger.debug("converted to => key: {} / value: {} / {}", name, convertedValue, convertedValue.getClass());
                    profile.addAttribute(name, convertedValue);
                }
            } else {
                convertedValue = value;
                logger.debug("no conversion => key: {} / value: {} / {}", name, convertedValue, convertedValue.getClass());
                profile.addAttribute(name, convertedValue);
            }
        }
    }

    /**
     * Convert the attributes if necessary and add them to the profile.
     *
     * @param profile the profile
     * @param attributes the attributes
     */
    public void convertAndAdd(final CommonProfile profile, final Map<String, Object> attributes) {
        if (attributes != null) {
            for (final Map.Entry<String, Object> entry : attributes.entrySet()){
                final String key = entry.getKey();
                final Object value = entry.getValue();
                convertAndAdd(profile, key, value);
            }
        }
    }

    /**
     * Define the way to build the profile.
     *
     * @param profileFactory the way to build the profile
     */
    protected void setProfileFactory(final Function<Object[], P> profileFactory) {
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
