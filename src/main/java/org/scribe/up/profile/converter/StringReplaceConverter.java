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

import org.scribe.up.util.StringHelper;

/**
 * This class makes replacements in a String.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringReplaceConverter extends BaseConverter<String> {
    
    private String regex;
    
    private String replacement;
    
    public StringReplaceConverter(String regex, String replacement) {
        this.regex = regex;
        this.replacement = replacement;
    }
    
    @Override
    public String convert(Object attribute) {
        if (attribute != null && attribute instanceof String) {
            String s = (String) attribute;
            if (StringHelper.isNotBlank(s) && StringHelper.isNotBlank(regex) && StringHelper.isNotBlank(replacement)) {
                return s.replaceAll(regex, replacement);
            }
        }
        return null;
    }
}
