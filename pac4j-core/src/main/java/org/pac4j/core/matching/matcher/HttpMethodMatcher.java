package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Matching on HTTP methods.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public class HttpMethodMatcher implements Matcher {

    private Set<HttpConstants.HTTP_METHOD> methods;

    public HttpMethodMatcher() {}

    public HttpMethodMatcher(final HttpConstants.HTTP_METHOD... methods) {
        if (methods != null) {
            this.methods = new HashSet<>(Arrays.asList(methods));
        }
    }

    @Override
    public boolean matches(final WebContext context) {
        CommonHelper.assertNotNull("methods", methods);
        final String requestMethod = context.getRequestMethod();

        for (final HttpConstants.HTTP_METHOD method : methods) {
            if (method.name().equalsIgnoreCase(requestMethod)) {
                return true;
            }
        }
        return false;
    }

    public Set<HttpConstants.HTTP_METHOD> getMethods() {
        return methods;
    }

    public void setMethods(final Set<HttpConstants.HTTP_METHOD> methods) {
        this.methods = methods;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "methods", this.methods);
    }
}
