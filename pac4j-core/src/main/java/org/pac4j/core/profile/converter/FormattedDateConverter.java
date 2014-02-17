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

import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.FormattedDate;

/**
 * This class converts a String (depending on a specified format) into a FormattedDate.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FormattedDateConverter extends DateConverter {
    
    public FormattedDateConverter(final String format) {
        super(format);
    }
    
    public FormattedDateConverter(final String format, final Locale locale) {
        super(format, locale);
    }
    
    @Override
    public FormattedDate convert(final Object attribute) {
        final Object result = super.convert(attribute);
        if (result != null && result instanceof Date) {
            return new FormattedDate((Date) result, this.format, this.locale);
        }
        return null;
    }
}
