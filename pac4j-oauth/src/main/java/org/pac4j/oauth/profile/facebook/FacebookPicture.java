package org.pac4j.oauth.profile.facebook;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.pac4j.oauth.profile.JsonObject;

/**
 * This class represents a Facebook picture.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookPicture extends JsonObject {
    
    private static final long serialVersionUID = -797546775636792491L;
    
    private String url;

    @JsonProperty("is_silhouette")
    private Boolean isSilhouette;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getSilhouette() {
        return isSilhouette;
    }

    public void setSilhouette(Boolean silhouette) {
        isSilhouette = silhouette;
    }
}
