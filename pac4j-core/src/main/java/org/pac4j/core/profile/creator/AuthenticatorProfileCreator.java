package org.pac4j.core.profile.creator;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;

/**
 * This profile creator retrieves the user profile attached in the {@link org.pac4j.core.credentials.Credentials}.
 * Using the {@link #setProfileFactory(java.util.function.Supplier)} method, a new type of profile can be returned.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AuthenticatorProfileCreator<C extends Credentials, P extends CommonProfile> extends AbstractProfileCreator<C, P> {

    public final static AuthenticatorProfileCreator INSTANCE = new AuthenticatorProfileCreator<>();

    @Override
    protected void internalInit(final WebContext context) {}

    @Override
    public P create(final C credentials, final WebContext context) throws HttpAction {
        final P profile = (P) credentials.getUserProfile();
        if (getProfileFactory() == null) {
            return profile;
        } else {
            // rebuild the new profile type
            final P newProfile = getProfileFactory().get();
            newProfile.setId(profile.getId());
            newProfile.addAttributes(profile.getAttributes());
            newProfile.addRoles(profile.getRoles());
            newProfile.addPermissions(profile.getPermissions());
            newProfile.setRemembered(profile.isRemembered());
            newProfile.setClientName(profile.getClientName());
            return newProfile;
        }
    }
}
