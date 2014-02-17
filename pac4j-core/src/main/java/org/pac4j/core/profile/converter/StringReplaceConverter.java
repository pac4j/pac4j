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

import org.pac4j.core.util.CommonHelper;

/**
 * This class makes replacements in a String.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringReplaceConverter implements AttributeConverter<String> {
    
    private final String regex;
    
    private final String replacement;
    
    public StringReplaceConverter(final String regex, final String replacement) {
        this.regex = regex;
        this.replacement = replacement;
    }
    
    public String convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            final String s = (String) attribute;
            if (CommonHelper.isNotBlank(s) && CommonHelper.isNotBlank(this.regex)
                && CommonHelper.isNotBlank(this.replacement)) {
                return s.replaceAll(this.regex, this.replacement);
            }
        }
        return null;
    }
}
