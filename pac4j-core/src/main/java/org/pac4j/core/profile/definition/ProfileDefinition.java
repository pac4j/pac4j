package org.pac4j.core.profile.definition;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.util.CommonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Define a profile (its class and attributes).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class ProfileDefinition<P extends CommonProfile> {

    private final List<String> primaries = new ArrayList<>();

    private final List<String> secondaries = new ArrayList<>();

    private final Map<String, AttributeConverter<? extends Object>> converters = new HashMap<>();

    protected Supplier<P> newProfile = () -> (P) new CommonProfile();

    /**
     * Return a new profile instance.
     *
     * @return the new profile instance
     */
    public P newProfile() {
        return newProfile.get();
    }

    /**
     * Convert an attribute into the right type. If no converter exists for this attribute name, the attribute is returned.
     *
     * @param name name of the attribute
     * @param value value of the attribute
     * @return the converted attribute or the attribute if no converter exists for this attribute name
     */
    public Object convert(final String name, final Object value) {
        final AttributeConverter<? extends Object> converter = this.converters.get(name);
        if (converter != null && value != null) {
            return converter.convert(value);
        } else {
            return value;
        }
    }

    /**
     * Define the way to build the profile.
     *
     * @param profileFactory the way to build the profile
     */
    protected void setProfileFactory(final Supplier<P> profileFactory) {
        CommonHelper.assertNotNull("profileFactory", profileFactory);
        CommonHelper.assertNotNull("profileFactory.get()", profileFactory.get());
        if (this.newProfile == null) {
            this.newProfile = profileFactory;
        }
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
}
