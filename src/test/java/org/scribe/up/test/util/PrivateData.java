/*
  Copyright 2012 Jérôme Leleu

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
package org.scribe.up.test.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides private data like keys, secrets, logins and passwords. All these data are read from a properties file, defined by a
 * system property.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class PrivateData {
    
    private static final Logger logger = LoggerFactory.getLogger(PrivateData.class);
    
    private static Properties properties = new Properties();
    
    static {
        try {
            properties.load(new FileInputStream(System.getProperty("scribe-up.properties")));
        } catch (IOException e) {
            logger.warn("Cannot load properties : ", e);
        }
    }
    
    public static String get(String key) {
        return properties.getProperty(key);
    }
}
