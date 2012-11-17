package org.scribe.up.profile.facebook;

import org.scribe.up.profile.JsonObject;
import org.scribe.up.profile.converter.Converters;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a Facebook picture.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class FacebookPicture extends JsonObject {
    
    private static final long serialVersionUID = -797546775636792491L;
    
    private String url;
    
    private Boolean isSilhouette;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.url = Converters.stringConverter.convertFromJson(json, "url");
        this.isSilhouette = Converters.booleanConverter.convertFromJson(json, "is_silhouette");
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public Boolean getIsSilhouette() {
        return this.isSilhouette;
    }
}
