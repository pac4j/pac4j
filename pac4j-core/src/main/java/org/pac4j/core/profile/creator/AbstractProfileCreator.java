package org.pac4j.core.profile.creator;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

import java.util.function.Supplier;

/**
 * Abstract profile creator where you can define the profile created.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public abstract class AbstractProfileCreator<C extends Credentials, P extends CommonProfile> extends InitializableWebObject implements ProfileCreator<C, P> {

    private Supplier<P> profileFactory;

    public Supplier<P> getProfileFactory() {
        return profileFactory;
    }

    public void setProfileFactory(final Supplier<P> profileFactory) {
        CommonHelper.assertNotNull("profileFactory", profileFactory);
        if (this.profileFactory == null) {
            this.profileFactory = profileFactory;
        }
    }
}
