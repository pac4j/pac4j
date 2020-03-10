package org.pac4j.oauth.profile.facebook;

import java.io.Serializable;

/**
 * This class represents a Facebook music data : song, musician or radio_station.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookMusicData implements Serializable {
    
    private static final long serialVersionUID = 3242237840580051260L;
    
    private String id;
    
    private String url;
    
    private String type;
    
    private String title;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
