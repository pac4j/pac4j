package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.CommonProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An authorizer to require elements.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public abstract class AbstractRequireElementAuthorizer<E extends Object, U extends CommonProfile> extends SingleProfileAuthorizer<U> {

    protected Set<E> elements;

    /**
     * Check a specific element.
     *
     * @param context the web context
     * @param profile the profile
     * @param element the element to check
     * @return whether it is authorized for this element
     * @throws RequiresHttpAction whether an additional HTTP action is required
     */
    protected abstract boolean check(final WebContext context, final U profile, final E element) throws RequiresHttpAction;

    public Set<E> getElements() {
        return elements;
    }

    public void setElements(final Set<E> elements) {
        this.elements = elements;
    }

    public void setElements(final List<E> elements) {
        if (elements != null) {
            this.elements = new HashSet<>(elements);
        }
    }

    public void setElements(final E... elements) {
        if (elements != null) {
            setElements(Arrays.asList(elements));
        }
    }
}
