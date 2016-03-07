package org.pac4j.oauth.profile;

import java.io.Serializable;

/**
 * Return an Object as a JSON string.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class JsonObject implements Serializable {

    public String toString() {
        return JsonHelper.toJSONString(this);
    }
}
