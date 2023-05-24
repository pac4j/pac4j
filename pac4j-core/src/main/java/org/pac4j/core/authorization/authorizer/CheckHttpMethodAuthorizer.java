package org.pac4j.core.authorization.authorizer;

import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.profile.UserProfile;

import java.util.List;
import java.util.Set;

/**
 * Checks the HTTP method.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CheckHttpMethodAuthorizer extends AbstractRequireAnyAuthorizer<HttpConstants.HTTP_METHOD> {

    /**
     * <p>Constructor for CheckHttpMethodAuthorizer.</p>
     */
    public CheckHttpMethodAuthorizer() { }

    /**
     * <p>Constructor for CheckHttpMethodAuthorizer.</p>
     *
     * @param methods a {@link HttpConstants.HTTP_METHOD} object
     */
    public CheckHttpMethodAuthorizer(final HttpConstants.HTTP_METHOD... methods) {
        setElements(methods);
    }

    /**
     * <p>Constructor for CheckHttpMethodAuthorizer.</p>
     *
     * @param methods a {@link List} object
     */
    public CheckHttpMethodAuthorizer(final List<HttpConstants.HTTP_METHOD> methods) {
        setElements(methods);
    }

    /**
     * <p>Constructor for CheckHttpMethodAuthorizer.</p>
     *
     * @param methods a {@link Set} object
     */
    public CheckHttpMethodAuthorizer(final Set<HttpConstants.HTTP_METHOD> methods) {
        setElements(methods);
    }

    /** {@inheritDoc} */
    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile,
                            final HttpConstants.HTTP_METHOD element) {
        val requestMethod = context.getRequestMethod();
        return requestMethod.equalsIgnoreCase(element.toString());
    }

    /**
     * <p>checkHttpMethod.</p>
     *
     * @param methods a {@link HttpConstants.HTTP_METHOD} object
     * @return a {@link CheckHttpMethodAuthorizer} object
     */
    public static CheckHttpMethodAuthorizer checkHttpMethod(HttpConstants.HTTP_METHOD... methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }

    /**
     * <p>checkHttpMethod.</p>
     *
     * @param methods a {@link List} object
     * @return a {@link CheckHttpMethodAuthorizer} object
     */
    public static CheckHttpMethodAuthorizer checkHttpMethod(List<HttpConstants.HTTP_METHOD> methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }

    /**
     * <p>checkHttpMethod.</p>
     *
     * @param methods a {@link Set} object
     * @return a {@link CheckHttpMethodAuthorizer} object
     */
    public static CheckHttpMethodAuthorizer checkHttpMethod(Set<HttpConstants.HTTP_METHOD> methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }
}
