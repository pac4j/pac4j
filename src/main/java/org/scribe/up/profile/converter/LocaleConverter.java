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

import java.util.Locale;

/**
 * This class converts a String to a Locale.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class LocaleConverter extends BaseConverter<Locale> {
    
    @Override
    public Locale convert(Object attribute) {
        if (attribute != null && attribute instanceof String) {
            String s = ((String) attribute).replaceAll("-", "_");
            String[] parts = s.split("_");
            int length = parts.length;
            if (length == 2) {
                return new Locale(parts[0], parts[1]);
            } else if (length == 1) {
                return new Locale(parts[0]);
            }
        }
        return null;
    }
}
