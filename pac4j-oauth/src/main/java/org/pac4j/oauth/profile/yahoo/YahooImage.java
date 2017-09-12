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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
