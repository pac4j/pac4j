package org.pac4j.core.authorization.authorizer;

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

    public CheckHttpMethodAuthorizer() { }

    public CheckHttpMethodAuthorizer(final HttpConstants.HTTP_METHOD... methods) {
        setElements(methods);
    }

    public CheckHttpMethodAuthorizer(final List<HttpConstants.HTTP_METHOD> methods) {
        setElements(methods);
    }

    public CheckHttpMethodAuthorizer(final Set<HttpConstants.HTTP_METHOD> methods) {
        setElements(methods);
    }

    @Override
    protected boolean check(final WebContext context, final SessionStore sessionStore, final UserProfile profile,
                            final HttpConstants.HTTP_METHOD element) {
        final var requestMethod = context.getRequestMethod();
        return requestMethod.equalsIgnoreCase(element.toString());
    }

    public static CheckHttpMethodAuthorizer checkHttpMethod(HttpConstants.HTTP_METHOD... methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }

    public static CheckHttpMethodAuthorizer checkHttpMethod(List<HttpConstants.HTTP_METHOD> methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }

    public static CheckHttpMethodAuthorizer checkHttpMethod(Set<HttpConstants.HTTP_METHOD> methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }
}
