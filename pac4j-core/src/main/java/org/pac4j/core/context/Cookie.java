package org.pac4j.core.context;

import org.pac4j.core.exception.TechnicalException;

/**
 * @author Misagh Moayyed
 * @since 1.8.1
 */
public final class Cookie {

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

}
