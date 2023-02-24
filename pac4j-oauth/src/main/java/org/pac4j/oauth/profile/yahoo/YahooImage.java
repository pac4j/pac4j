package org.pac4j.oauth.profile.yahoo;

import java.io.Serializable;

/**
 * This class represents a Yahoo image.
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class YahooImage implements Serializable {

    private static final long serialVersionUID = 5378229852266815223L;

    private String imageUrl;

    private Integer width;

    private Integer height;

    private String size;

    /**
     * <p>Getter for the field <code>imageUrl</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * <p>Setter for the field <code>imageUrl</code>.</p>
     *
     * @param imageUrl a {@link java.lang.String} object
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * <p>Getter for the field <code>width</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * <p>Setter for the field <code>width</code>.</p>
     *
     * @param width a {@link java.lang.Integer} object
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * <p>Getter for the field <code>height</code>.</p>
     *
     * @return a {@link java.lang.Integer} object
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * <p>Setter for the field <code>height</code>.</p>
     *
     * @param height a {@link java.lang.Integer} object
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * <p>Getter for the field <code>size</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getSize() {
        return size;
    }

    /**
     * <p>Setter for the field <code>size</code>.</p>
     *
     * @param size a {@link java.lang.String} object
     */
    public void setSize(String size) {
        this.size = size;
    }
}
