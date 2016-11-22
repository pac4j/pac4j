package org.pac4j.oauth.profile.generic;

import org.pac4j.core.profile.AttributesDefinition;

import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This is the user profile for the generic OAuth 2.0 client.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.GenericOAuth20Client}  </p>
 *
 * @author aherrick
 * @since 1.9.2
 */
public class GenericOAuth20Profile extends OAuth20Profile {

    private transient AttributesDefinition attributes = new DefaultGenericAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return attributes;
    }

    /**
     * Used to set a custom AttributesDefinition.
     *
     * @param attributes the attributes definition
     */
    public void setAttributesDefinition(AttributesDefinition attributes) {
        this.attributes = attributes;
    }
}