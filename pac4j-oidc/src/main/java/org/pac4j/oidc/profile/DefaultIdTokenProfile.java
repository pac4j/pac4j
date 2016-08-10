package org.pac4j.oidc.profile;

import org.pac4j.core.profile.AttributesDefinition;

/**
 * Default JWT ID token profile.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class DefaultIdTokenProfile extends OidcProfile<DefaultIdTokenProfile> implements JwtIdTokenProfile {

    private static final long serialVersionUID = -1964465863340375258L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new OidcIdTokenAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
}
