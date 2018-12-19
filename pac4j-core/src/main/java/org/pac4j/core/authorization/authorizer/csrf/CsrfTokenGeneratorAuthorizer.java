package org.pac4j.core.authorization.authorizer.csrf;

import org.pac4j.core.authorization.authorizer.Authorizer;
import org.pac4j.core.context.Cookie;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.util.CommonHelper;

import java.util.List;

/**
 * Authorizer which creates a new CSRF token and adds it as a request attribute and as a cookie (AngularJS).
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CsrfTokenGeneratorAuthorizer implements Authorizer<UserProfile> {

    private CsrfTokenGenerator csrfTokenGenerator;

    private String domain;

    private String path = "/";

    private Boolean httpOnly;

    private Boolean secure;

    public CsrfTokenGeneratorAuthorizer(final CsrfTokenGenerator csrfTokenGenerator) {
        this.csrfTokenGenerator = csrfTokenGenerator;
    }

    @Override
    public boolean isAuthorized(final WebContext context, final List<UserProfile> profiles) {
        CommonHelper.assertNotNull("csrfTokenGenerator", csrfTokenGenerator);
        final String token = csrfTokenGenerator.get(context);
        context.setRequestAttribute(Pac4jConstants.CSRF_TOKEN, token);
        final Cookie cookie = new Cookie(Pac4jConstants.CSRF_TOKEN, token);
        if (domain != null) {
            cookie.setDomain(domain);
        } else {
            cookie.setDomain(context.getServerName());
        }
        if (path != null) {
            cookie.setPath(path);
        }
        if (httpOnly != null) {
            cookie.setHttpOnly(httpOnly.booleanValue());
        }
        if (secure != null) {
            cookie.setSecure(secure.booleanValue());
        }
        context.addResponseCookie(cookie);
        return true;
    }

    public CsrfTokenGenerator getCsrfTokenGenerator() {
        return csrfTokenGenerator;
    }

    public void setCsrfTokenGenerator(final CsrfTokenGenerator csrfTokenGenerator) {
        this.csrfTokenGenerator = csrfTokenGenerator;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public Boolean getHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(final Boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(final Boolean secure) {
        this.secure = secure;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "csrfTokenGenerator", csrfTokenGenerator, "domain", domain, "path", path,
            "httpOnly", httpOnly, "secure", secure);
    }
}
