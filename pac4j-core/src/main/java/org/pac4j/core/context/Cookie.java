package org.pac4j.core.context;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Misagh Moayyed
 * @since 1.8.1
 */
public final class Cookie {

    private static final ZoneId GMT = ZoneId.of("GMT");
    /**
     * Date formats with time zone as specified in the HTTP RFC to use for formatting.
     * @see <a href="https://tools.ietf.org/html/rfc7231#section-7.1.1.1">Section 7.1.1.1 of RFC 7231</a>
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(GMT);

    private String name;
    private String value;
    private String domain;
    private int maxAge = -1;
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
        var builder = new StringBuilder();
        builder.append(String.format("%s=%s;", cookie.getName(), cookie.getValue()));

        if (cookie.getMaxAge() > -1) {
            builder.append(String.format(" Max-Age=%s;", cookie.getMaxAge()));
            long millis = cookie.getMaxAge() > 0 ? System.currentTimeMillis() + (cookie.getMaxAge() * 1000) : 0;
            Instant instant = Instant.ofEpochMilli(millis);
            ZonedDateTime time = ZonedDateTime.ofInstant(instant, GMT);
            builder.append(String.format(" Expires=%s;", DATE_FORMATTER.format(time)));
        }
        if (CommonHelper.isNotBlank(cookie.getDomain())) {
            builder.append(String.format(" Domain=%s;", cookie.getDomain()));
        }
        builder.append(String.format(" Path=%s;", CommonHelper.isNotBlank(cookie.getPath()) ? cookie.getPath() : "/"));

        var sameSitePolicy = cookie.getSameSitePolicy() == null ? "none" : cookie.getSameSitePolicy().toLowerCase();
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
        var value = builder.toString();
        if (value.endsWith(";")) {
            value = value.substring(0, value.length() - 1);
        }
        response.addHeader("Set-Cookie", value);
    }
}
