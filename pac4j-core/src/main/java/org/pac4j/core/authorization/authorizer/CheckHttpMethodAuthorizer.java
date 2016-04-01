package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
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
public class CheckHttpMethodAuthorizer<U extends CommonProfile> extends AbstractRequireAnyAuthorizer<HTTP_METHOD, U> {

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
    protected boolean check(final WebContext context, final U profile, final HTTP_METHOD element) throws RequiresHttpAction {
        final String requestMethod = context.getRequestMethod();
        return requestMethod.equalsIgnoreCase(element.toString());
    }
}
