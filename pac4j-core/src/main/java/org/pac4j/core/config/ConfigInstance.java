/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.core.config;

import org.pac4j.core.exception.TechnicalException;

/**
 * One instance of the configuration. Useful in implementations where, for any reason,
 * the configuration must be shared across several elements.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class ConfigInstance {

    protected static Config config = new Config();

    public synchronized static void build(final String factoryName) {
        try {
            final Class<ConfigFactory> clazz = (Class<ConfigFactory>) Class.forName(factoryName);
            final ConfigFactory factory = clazz.newInstance();
            ConfigInstance.config = factory.build();
        } catch (final Exception e) {
            throw new TechnicalException(e);
        }
    }

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        ConfigInstance.config = config;
    }
}
