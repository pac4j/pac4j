package org.pac4j.core.matching.matcher.csrf;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.matching.matcher.Matcher;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

/**
 * Matcher which creates a new CSRF token and adds it as a request attribute and as a cookie (AngularJS).
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@Setter
@ToString
public class CsrfTokenGeneratorMatcher implements Matcher {

    private CsrfTokenGenerator csrfTokenGenerator;

    private String domain;

    private String path = "/";

    private Boolean httpOnly = true;

    private Boolean secure = true;

    private Integer maxAge;

    private String sameSitePolicy;

    private boolean addTokenAsAttribute = true;
    private boolean addTokenAsHeader = false;
    private boolean addTokenAsCookie = true;

    public CsrfTokenGeneratorMatcher(final CsrfTokenGenerator csrfTokenGenerator) {
        this.csrfTokenGenerator = csrfTokenGenerator;
    }

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        CommonHelper.assertNotNull("csrfTokenGenerator", csrfTokenGenerator);
        if (addTokenAsAttribute || addTokenAsHeader || addTokenAsCookie) {
            val token = csrfTokenGenerator.get(context, sessionStore);

            if (addTokenAsAttribute) {
                context.setRequestAttribute(Pac4jConstants.CSRF_TOKEN, token);
            }

            if (addTokenAsHeader) {
                context.setResponseHeader(Pac4jConstants.CSRF_TOKEN, token);
            }

            if (addTokenAsCookie) {
                val cookie = new Cookie(Pac4jConstants.CSRF_TOKEN, token);
                if (CommonHelper.isNotBlank(domain)) {
                    cookie.setDomain(domain);
                } else {
                    cookie.setDomain(context.getServerName());
                }
                if (CommonHelper.isNotBlank(path)) {
                    cookie.setPath(path);
                }
                if (httpOnly != null) {
                    cookie.setHttpOnly(httpOnly.booleanValue());
                }
                if (secure != null) {
                    cookie.setSecure(secure.booleanValue());
                }
                if (maxAge != null) {
                    cookie.setMaxAge(maxAge.intValue());
                }
                if (CommonHelper.isNotBlank(sameSitePolicy)) {
                    cookie.setSameSitePolicy(sameSitePolicy);
                }
                context.addResponseCookie(cookie);
            }
        }
        return true;
    }
}
