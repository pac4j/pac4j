package org.pac4j.oauth.profile.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * This class represents a Facebook picture.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookPicture implements Serializable {

    private static final long serialVersionUID = -797546775636792491L;

    private String url;

    @JsonProperty("is_silhouette")
    private Boolean isSilhouette;

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link java.lang.String} object
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>getSilhouette.</p>
     *
     * @return a {@link java.lang.Boolean} object
     */
    public Boolean getSilhouette() {
        return isSilhouette;
    }

    /**
     * <p>setSilhouette.</p>
     *
     * @param silhouette a {@link java.lang.Boolean} object
     */
    public void setSilhouette(Boolean silhouette) {
        isSilhouette = silhouette;
    }
}
