package org.pac4j.core.config;

/**
 * A singleton of the configuration. Useful in implementations where the configuration must be shared
 * and no dependency injection framework is available.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ConfigSingleton {

    private static Config config = new Config();

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        ConfigSingleton.config = config;
    }
}
