/*
  Copyright 2012  - 2013 Jerome Leleu

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
package org.pac4j.core.profile.converter;


/**
 * This class converts a String into a Boolean or returns the Boolean in input.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class BooleanConverter implements AttributeConverter<Boolean> {
    
    public Boolean convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof Boolean) {
                return (Boolean) attribute;
            } else if (attribute instanceof String) {
                return Boolean.parseBoolean((String) attribute);
            }
        }
        return null;
    }
}
