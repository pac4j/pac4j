package org.pac4j.core.context;

import org.pac4j.core.exception.TechnicalException;

/**
 * <p>Cookie class.</p>
 *
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
    private String comment;

    /**
     * <p>Constructor for Cookie.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param value a {@link java.lang.String} object
     */
    public Cookie(final String name, final String value) {
        if (name == null || name.length() == 0) {
            throw new TechnicalException("cookie name and value cannot be empty");
        }
        this.name = name;
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getValue() {
        return value;
    }

    /**
     * <p>Setter for the field <code>value</code>.</p>
     *
     * @param value a {@link java.lang.String} object
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>domain</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getDomain() {
        return domain;
    }

    /**
     * <p>Setter for the field <code>domain</code>.</p>
     *
     * @param domain a {@link java.lang.String} object
     */
    public void setDomain(final String domain) {
        this.domain = domain;
    }

    /**
     * <p>Getter for the field <code>maxAge</code>.</p>
     *
     * @return a int
     */
    public int getMaxAge() {
        return maxAge;
    }

    /**
     * <p>Setter for the field <code>maxAge</code>.</p>
     *
     * @param maxAge a int
     */
    public void setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * <p>Getter for the field <code>path</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPath() {
        return path;
    }

    /**
     * <p>Setter for the field <code>path</code>.</p>
     *
     * @param path a {@link java.lang.String} object
     */
    public void setPath(final String path) {
        this.path = path;
    }

    /**
     * <p>isSecure.</p>
     *
     * @return a boolean
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * <p>Setter for the field <code>secure</code>.</p>
     *
     * @param secure a boolean
     */
    public void setSecure(final boolean secure) {
        this.secure = secure;
    }

    /**
     * <p>isHttpOnly.</p>
     *
     * @return a boolean
     */
    public boolean isHttpOnly() {
        return isHttpOnly;
    }

    /**
     * <p>setHttpOnly.</p>
     *
     * @param httpOnly a boolean
     */
    public void setHttpOnly(final boolean httpOnly) {
        isHttpOnly = httpOnly;
    }

    /**
     * <p>Getter for the field <code>sameSitePolicy</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSameSitePolicy() { return sameSitePolicy; }

    /**
     * <p>Setter for the field <code>sameSitePolicy</code>.</p>
     *
     * @param sameSitePolicy a {@link java.lang.String} object
     */
    public void setSameSitePolicy(final String sameSitePolicy) { this.sameSitePolicy = sameSitePolicy; }

    /**
     * <p>Getter for the field <code>comment</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getComment() {
        return comment;
    }

    /**
     * <p>Setter for the field <code>comment</code>.</p>
     *
     * @param comment a {@link java.lang.String} object
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }
}
