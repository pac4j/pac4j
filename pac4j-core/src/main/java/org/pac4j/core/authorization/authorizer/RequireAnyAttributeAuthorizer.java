package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Collection;

/**
 * This is {@link RequireAnyAttributeAuthorizer} which checks profile
 * attributes for the specified element, and optionally
 * may pattern-check the configured value. In practice, you may
 * use this authorizer to see if the profile contains attribute X
 * and optionally, whether X has a value that matches pattern Y.
 *
 * @author Misagh Moayyed
 * @since 1.9.2
 */
public class RequireAnyAttributeAuthorizer<U extends UserProfile> extends AbstractRequireAnyAuthorizer<String, U> {
    private final String valueToMatch;

    public RequireAnyAttributeAuthorizer() {
        this(".+");
    }

    public RequireAnyAttributeAuthorizer(final String valueToMatch) {
        this.valueToMatch = valueToMatch;
    }

    @Override
    protected boolean check(final WebContext context, final U profile, final String element) {
        if (!profile.containsAttribute(element)) {
            return false;
        }

        if (CommonHelper.isBlank(this.valueToMatch)) {
            return true;
        }

        final Object attributeValues = profile.getAttribute(element);
        if (attributeValues instanceof Collection) {
            return Collection.class.cast(attributeValues)
                    .stream()
                    .filter(v -> v.toString().matches(this.valueToMatch))
                    .findAny()
                    .isPresent();
        }
        return attributeValues.toString().matches(this.valueToMatch);
    }

    public static <U extends UserProfile> RequireAnyAttributeAuthorizer<U> requireAnyAttribute(String valueToMatch) {
        return new RequireAnyAttributeAuthorizer<>(valueToMatch);
    }
}
