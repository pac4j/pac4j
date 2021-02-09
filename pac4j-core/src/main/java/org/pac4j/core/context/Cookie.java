package org.pac4j.core.context;

import java.util.Date;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Misagh Moayyed
 * @since 1.8.1
 */
public final class Cookie {

    private String name;
    private String value;
    private int version = 0;
    private String comment;
    private String domain;
    private int maxAge = -1;
    private Date expiry;
    private String path;
    private boolean secure;
    private boolean isHttpOnly = false;
    private String sameSitePolicy;

    public Cookie(final String name, final String value) {
        if (name == null || name.length() == 0) {
            throw new TechnicalException("cookie name and value cannot be empty");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public Date getExpiry() {
        return expiry == null ? null : new Date(expiry.getTime());
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry == null ? null : new Date(expiry.getTime());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return isHttpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        isHttpOnly = httpOnly;
    }

    public String getSameSitePolicy() { return sameSitePolicy; }

    public void setSameSitePolicy(String sameSitePolicy) { this.sameSitePolicy = sameSitePolicy; }

    public static void addCookieHeaderToResponse(Cookie cookie, final HttpServletResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s=%s;", cookie.getName(), cookie.getValue()));

        if (cookie.getMaxAge() > -1) {
            builder.append(String.format(" Max-Age=%s;", cookie.getMaxAge()));
        }
        if (CommonHelper.isNotBlank(cookie.getDomain())) {
            builder.append(String.format(" Domain=%s;", cookie.getDomain()));
        }
        builder.append(String.format(" Path=%s;", CommonHelper.isNotBlank(cookie.getPath()) ? cookie.getPath() : "/"));

        String sameSitePolicy = cookie.getSameSitePolicy() == null ? "none" : cookie.getSameSitePolicy().toLowerCase();
        switch (sameSitePolicy) {
            case "strict":
                builder.append(" SameSite=Strict;");
                break;
            case "lax":
                builder.append(" SameSite=Lax;");
                break;
            case "none":
            default:
                builder.append(" SameSite=None;");
                break;
        }
        if (cookie.isSecure() || "none".equals(sameSitePolicy)) {
            builder.append(" Secure;");
        }
        if (cookie.isHttpOnly()) {
            builder.append(" HttpOnly;");
        }
        String value = builder.toString();
        if (value.endsWith(";")) {
            value = value.substring(0, value.length() - 1);
        }
        response.addHeader("Set-Cookie", value);
    }
}
