package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;

import java.util.List;
import java.util.Set;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Checks the HTTP method.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CheckHttpMethodAuthorizer extends AbstractRequireAnyAuthorizer<HTTP_METHOD, CommonProfile> {

    public CheckHttpMethodAuthorizer() { }

    public CheckHttpMethodAuthorizer(final HTTP_METHOD... methods) {
        setElements(methods);
    }

    public CheckHttpMethodAuthorizer(final List<HTTP_METHOD> methods) {
        setElements(methods);
    }

    public CheckHttpMethodAuthorizer(final Set<HTTP_METHOD> methods) {
        setElements(methods);
    }

    @Override
    protected boolean check(final WebContext context, final CommonProfile profile, final HTTP_METHOD element) {
        final String requestMethod = context.getRequestMethod();
        return requestMethod.equalsIgnoreCase(element.toString());
    }

    public static CheckHttpMethodAuthorizer checkHttpMethod(HTTP_METHOD... methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }

    public static CheckHttpMethodAuthorizer checkHttpMethod(List<HTTP_METHOD> methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }

    public static CheckHttpMethodAuthorizer checkHttpMethod(Set<HTTP_METHOD> methods) {
        return new CheckHttpMethodAuthorizer(methods);
    }
}
