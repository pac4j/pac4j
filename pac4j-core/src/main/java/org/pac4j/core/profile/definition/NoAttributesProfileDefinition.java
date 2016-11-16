package org.pac4j.core.profile.definition;

import org.pac4j.core.profile.CommonProfile;

import java.util.function.Supplier;

/**
 * Profile definition with no attributes.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class NoAttributesProfileDefinition<P extends CommonProfile> extends ProfileDefinition<P> {

    public NoAttributesProfileDefinition() {}

    public NoAttributesProfileDefinition(final Supplier<P> profileFactory) {
        setProfileFactory(profileFactory);
    }
}
