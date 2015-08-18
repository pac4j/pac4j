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
