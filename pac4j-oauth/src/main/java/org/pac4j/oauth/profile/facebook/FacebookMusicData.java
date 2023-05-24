package org.pac4j.oauth.profile.facebook;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a Facebook music data : song, musician or radio_station.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookMusicData implements Serializable {

    @Serial
    private static final long serialVersionUID = 3242237840580051260L;

    private String id;

    private String url;

    private String type;

    private String title;

    /**
     * <p>Getter for the field <code>id</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getId() {
        return id;
    }

    /**
     * <p>Setter for the field <code>id</code>.</p>
     *
     * @param id a {@link String} object
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * <p>Getter for the field <code>url</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getUrl() {
        return url;
    }

    /**
     * <p>Setter for the field <code>url</code>.</p>
     *
     * @param url a {@link String} object
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * <p>Getter for the field <code>type</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getType() {
        return type;
    }

    /**
     * <p>Setter for the field <code>type</code>.</p>
     *
     * @param type a {@link String} object
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <p>Getter for the field <code>title</code>.</p>
     *
     * @return a {@link String} object
     */
    public String getTitle() {
        return title;
    }

    /**
     * <p>Setter for the field <code>title</code>.</p>
     *
     * @param title a {@link String} object
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
