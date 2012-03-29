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
package org.scribe.up.profile.converter;

import org.scribe.up.profile.Gender;

/**
 * This class is the converter of a String to a Gender.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverter implements AttributeConverter<Gender> {
    
    private String maleText;
    
    private String femaleText;
    
    public GenderConverter(String maleText, String femaleText) {
        this.maleText = maleText;
        this.femaleText = femaleText;
    }
    
    public Gender convert(Object attribute) {
        if (attribute != null && attribute instanceof String) {
            String s = ((String) attribute).toLowerCase();
            if (s.equals(maleText)) {
                return Gender.MALE;
            } else if (s.equals(femaleText)) {
                return Gender.FEMALE;
            } else {
                return Gender.UNSPECIFIED;
            }
        }
        return null;
    }
}
