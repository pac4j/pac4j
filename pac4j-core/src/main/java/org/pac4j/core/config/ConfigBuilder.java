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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * To build a configuration from a factory.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ConfigBuilder {

    private final static Logger logger = LoggerFactory.getLogger(ConfigBuilder.class);

    public synchronized static Config build(final String factoryName) {
        try {
            logger.info("Build the configuration from factory: {}", factoryName);
            final Class<ConfigFactory> clazz = (Class<ConfigFactory>) Class.forName(factoryName);
            final ConfigFactory factory = clazz.newInstance();
            return factory.build();
        } catch (final Exception e) {
            throw new TechnicalException("Cannot build configuration", e);
        }
    }
}
