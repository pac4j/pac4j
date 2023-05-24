package org.pac4j.oauth.profile.facebook;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a common Facebook object (id + name).
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class FacebookObject implements Serializable {

    @Serial
    private static final long serialVersionUID = 7393867411970930893L;

    private String id;

    private String name;

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link String} object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     *
     * @param name a {@link String} object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getId() {
        return this.id;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getName() {
        return this.name;
    }
}
