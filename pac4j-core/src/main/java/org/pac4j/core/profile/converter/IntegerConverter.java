/*
  Copyright 2012 - 2014 Jerome Leleu

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
 * This class converts a String into an Integer or returns the Integer in input.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class IntegerConverter implements AttributeConverter<Integer> {
    
    public Integer convert(final Object attribute) {
        if (attribute != null) {
            if (attribute instanceof Integer) {
                return (Integer) attribute;
            } else if (attribute instanceof String) {
                return Integer.parseInt((String) attribute);
            }
        }
        return null;
    }
}
