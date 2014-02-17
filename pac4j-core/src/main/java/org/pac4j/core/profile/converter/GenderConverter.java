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

import org.pac4j.core.profile.Gender;

/**
 * This class converts a String to a Gender.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverter implements AttributeConverter<Gender> {
    
    private final String maleText;
    
    private final String femaleText;
    
    public GenderConverter(final String maleText, final String femaleText) {
        this.maleText = maleText;
        this.femaleText = femaleText;
    }
    
    public Gender convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            final String s = ((String) attribute).toLowerCase();
            if (s.equals(this.maleText) || Gender.MALE.toString().toLowerCase().equals(s)) {
                return Gender.MALE;
            } else if (s.equals(this.femaleText) || Gender.FEMALE.toString().toLowerCase().equals(s)) {
                return Gender.FEMALE;
            } else {
                return Gender.UNSPECIFIED;
            }
        }
        return null;
    }
}
