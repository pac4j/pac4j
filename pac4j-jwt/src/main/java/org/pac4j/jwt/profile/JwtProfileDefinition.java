package org.pac4j.jwt.profile;

import net.minidev.json.JSONObject;
import org.pac4j.core.profile.AttributeLocation;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * This is {@link JwtProfileDefinition}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */
public class JwtProfileDefinition extends CommonProfileDefinition<JwtProfile> {
    private boolean keepNestedAttributes = true;

    public JwtProfileDefinition() {
        super(x -> new JwtProfile());
    }

    @Override
    public void convertAndAdd(final CommonProfile profile, final AttributeLocation attributeLocation,
                              final String name, final Object value) {
        if (value instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) value;
            jsonObject.forEach((key, objectValue) -> super.convertAndAdd(profile, attributeLocation, key, objectValue));
            if (keepNestedAttributes) {
                super.convertAndAdd(profile, attributeLocation, name, value);
            }
        } else {
            super.convertAndAdd(profile, attributeLocation, name, value);
        }
    }

    public void setKeepNestedAttributes(boolean keepNestedAttributes) {
        this.keepNestedAttributes = keepNestedAttributes;
    }
}


