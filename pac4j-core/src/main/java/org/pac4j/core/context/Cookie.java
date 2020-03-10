package org.pac4j.core.context;

import java.util.Date;

import org.pac4j.core.exception.TechnicalException;

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

    public void setName(final String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }

    public Date getExpiry() {
        return expiry == null ? null : new Date(expiry.getTime());
    }

    public void setExpiry(final Date expiry) {
        this.expiry = expiry == null ? null : new Date(expiry.getTime());
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(final boolean secure) {
        this.secure = secure;
    }


    public boolean isHttpOnly() {
        return isHttpOnly;
    }

    public void setHttpOnly(final boolean httpOnly) {
        isHttpOnly = httpOnly;
    }
}
