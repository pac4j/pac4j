package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;

/**
 * This class represents a Facebook group.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookGroup implements Serializable {

    private static final long serialVersionUID = -846266834053161809L;

    private String id;

    private String name;

    private String privacy;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.String} object
     */
    public void setId(String id) {
        this.id = id;
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
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>privacy</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getPrivacy() {
        return privacy;
    }

    /**
     * <p>Setter for the field <code>privacy</code>.</p>
     *
     * @param privacy a {@link java.lang.String} object
     */
    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }
}
