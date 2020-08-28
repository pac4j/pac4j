package org.pac4j.core.client.config;

import org.pac4j.core.util.InitializableObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Base parent class for all client configurations
 * to hold common fields or to be used as a common parent
 * for type checking and casts in customizations.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public abstract class BaseClientConfiguration extends InitializableObject {

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(final Map<String, String> customParams) {
        this.customParams = customParams;
    }

    public void addCustomParam(final String name, final String value) {
        this.customParams.put(name, value);
    }
}
