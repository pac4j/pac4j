package org.pac4j.core.authorization.authorizer;

import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.Collection;

/**
 * This is {@link org.pac4j.core.authorization.authorizer.RequireAnyAttributeAuthorizer} which checks profile
 * attributes for the specified element, and optionally
 * may pattern-check the configured value. In practice, you may
 * use this authorizer to see if the profile contains attribute X
 * and optionally, whether X has a value that matches pattern Y.
 *
 * @author Misagh Moayyed
 * @since 1.9.2
 */
public class RequireAnyAttributeAuthorizer extends AbstractRequireAnyAuthorizer<String> {
    private final String valueToMatch;

    /**
     * <p>Constructor for RequireAnyAttributeAuthorizer.</p>
     */
    public RequireAnyAttributeAuthorizer() {
        this(".+");
    }

    /**
     * <p>Constructor for RequireAnyAttributeAuthorizer.</p>
     *
     * @param valueToMatch a {@link java.lang.String} object
     */
    public RequireAnyAttributeAuthorizer(final String valueToMatch) {
        this.valueToMatch = valueToMatch;
    }

    /** {@inheritDoc} */
    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile, final String element) {
        if (!profile.containsAttribute(element)) {
            return false;
        }

        if (CommonHelper.isBlank(this.valueToMatch)) {
            return true;
        }

        val attributeValues = profile.getAttribute(element);
        if (attributeValues instanceof Collection) {
            return Collection.class.cast(attributeValues)
                    .stream()
                    .anyMatch(v -> v.toString().matches(this.valueToMatch));
        }
        return attributeValues.toString().matches(this.valueToMatch);
    }

    /**
     * <p>requireAnyAttribute.</p>
     *
     * @param valueToMatch a {@link java.lang.String} object
     * @return a {@link org.pac4j.core.authorization.authorizer.RequireAnyAttributeAuthorizer} object
     */
    public static RequireAnyAttributeAuthorizer requireAnyAttribute(String valueToMatch) {
        return new RequireAnyAttributeAuthorizer(valueToMatch);
    }
}
