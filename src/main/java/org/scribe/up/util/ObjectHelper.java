/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.util;

/**
 * This class is an helper to return default value instead of object.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ObjectHelper {
    
    private ObjectHelper() {
    }
    
    /**
     * Return the default value if object is null.
     * 
     * @param object
     * @param defaultValue
     * @return the default value if object is null
     */
    public static Object getDefaultIfNull(Object object, Object defaultValue) {
        if (object != null) {
            return object;
        } else {
            return defaultValue;
        }
    }
}
