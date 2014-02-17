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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a String (depending on a specified format) into a Date.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class DateConverter implements AttributeConverter<Date> {
    
    protected static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
    
    protected String format;
    
    protected Locale locale;
    
    public DateConverter(final String format) {
        this.format = format;
    }
    
    public DateConverter(final String format, final Locale locale) {
        this.format = format;
        this.locale = locale;
    }
    
    public Date convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            SimpleDateFormat simpleDateFormat;
            if (this.locale == null) {
                simpleDateFormat = new SimpleDateFormat(this.format);
            } else {
                simpleDateFormat = new SimpleDateFormat(this.format, this.locale);
            }
            final String s = (String) attribute;
            try {
                return simpleDateFormat.parse(s);
            } catch (final ParseException e) {
                logger.error("parse exception on " + s + " with format : " + this.format, e);
            }
        }
        return null;
    }
}
