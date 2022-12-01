package org.pac4j.core.matching.matcher;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
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
@Getter
@Setter
@ToString
public class HttpMethodMatcher implements Matcher {

    private Set<HttpConstants.HTTP_METHOD> methods;

    public HttpMethodMatcher() {}

    public HttpMethodMatcher(final HttpConstants.HTTP_METHOD... methods) {
        if (methods != null) {
            this.methods = new HashSet<>(Arrays.asList(methods));
        }
    }

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        CommonHelper.assertNotNull("methods", methods);
        val requestMethod = context.getRequestMethod();

        for (val method : methods) {
            if (method.name().equalsIgnoreCase(requestMethod)) {
                return true;
            }
        }
        return false;
    }
}
