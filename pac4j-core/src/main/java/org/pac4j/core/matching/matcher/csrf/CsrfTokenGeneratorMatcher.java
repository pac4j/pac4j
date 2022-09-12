package org.pac4j.core.matching.matcher.csrf;

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
            final var token = csrfTokenGenerator.get(context, sessionStore);

            if (addTokenAsAttribute) {
                context.setRequestAttribute(Pac4jConstants.CSRF_TOKEN, token);
            }

            if (addTokenAsHeader) {
                context.setResponseHeader(Pac4jConstants.CSRF_TOKEN, token);
            }

            if (addTokenAsCookie) {
                final var cookie = new Cookie(Pac4jConstants.CSRF_TOKEN, token);
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

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getSameSitePolicy() { return sameSitePolicy; }

    public void setSameSitePolicy(String sameSitePolicy) { this.sameSitePolicy = sameSitePolicy; }

    public boolean isAddTokenAsAttribute() {
        return addTokenAsAttribute;
    }

    public void setAddTokenAsAttribute(final boolean addTokenAsAttribute) {
        this.addTokenAsAttribute = addTokenAsAttribute;
    }

    public boolean isAddTokenAsHeader() {
        return addTokenAsHeader;
    }

    public void setAddTokenAsHeader(final boolean addTokenAsHeader) {
        this.addTokenAsHeader = addTokenAsHeader;
    }

    public boolean isAddTokenAsCookie() {
        return addTokenAsCookie;
    }

    public void setAddTokenAsCookie(final boolean addTokenAsCookie) {
        this.addTokenAsCookie = addTokenAsCookie;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "csrfTokenGenerator", csrfTokenGenerator, "domain", domain, "path", path,
            "httpOnly", httpOnly, "secure", secure, "maxAge", maxAge, "addTokenAsAttribute", addTokenAsAttribute,
            "addTokenAsHeader", addTokenAsHeader, "addTokenAsCookie", addTokenAsCookie);
    }
}
