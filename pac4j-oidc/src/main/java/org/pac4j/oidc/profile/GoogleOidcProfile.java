package org.pac4j.oidc.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oidc.profile.google.GoogleOidcAttributesDefinition;

/**
 * <p>This class is the user profile for Google (using OpenID Connect protocol) with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oidc.client.GoogleOidcClient}.</p>
 *
 * @author Jerome Leleu
 * @version 1.9.0
 */
public class GoogleOidcProfile extends OidcProfile<GoogleIdTokenProfile> {

    private static final long serialVersionUID = -6076954328349948251L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new GoogleOidcAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }

    @Override
    public String getFirstName() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.GIVEN_NAME);
    }

    @Override
    public String getDisplayName() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.NAME);
    }

    @Override
    public String getPictureUrl() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.PICTURE);
    }

    @Override
    public String getProfileUrl() {
        return (String) getAttribute(GoogleOidcAttributesDefinition.PROFILE);
    }

    public Boolean getEmailVerified() {
        return (Boolean) getAttribute(GoogleOidcAttributesDefinition.EMAIL_VERIFIED);
    }

    @Override
    protected GoogleIdTokenProfile buildJwtIdTokenProfile() {
        return new GoogleIdTokenProfile();
    }
}
