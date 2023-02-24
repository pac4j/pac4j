package org.pac4j.oauth.profile.yahoo;

import java.io.Serializable;

/**
 * This class represents a Yahoo email.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooEmail implements Serializable {

    private static final long serialVersionUID = 1195905995057732685L;

    private Integer id;

    private Boolean primary;

    private String handle;

    private String type;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link java.lang.Integer} object
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>primary</code>.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getPrimary() {
        return primary;
    }

    /**
     * <p>Setter for the field <code>primary</code>.</p>
     *
     * @param primary a {@link java.lang.Boolean} object
     */
    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    /**
     * <p>Getter for the field <code>handle</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getHandle() {
        return handle;
    }

    /**
     * <p>Setter for the field <code>handle</code>.</p>
     *
     * @param handle a {@link java.lang.String} object
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link java.lang.String} object
     */
    public void setType(String type) {
        this.type = type;
    }
}
