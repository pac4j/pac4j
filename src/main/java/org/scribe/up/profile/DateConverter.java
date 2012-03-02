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
package org.scribe.up.profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a String (depending on a specified format), an Integer or a Long into a Date.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class DateConverter implements AttributeConverter<Date> {
    
    private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
    
    private String format;
    
    private Locale locale;
    
    public DateConverter() {
    }
    
    public DateConverter(String format) {
        this.format = format;
    }
    
    public DateConverter(String format, Locale locale) {
        this.format = format;
        this.locale = locale;
    }
    
    public Date convert(Object attribute) {
        if (attribute != null) {
            if (attribute instanceof String && format != null) {
                SimpleDateFormat simpleDateFormat;
                if (locale == null) {
                    simpleDateFormat = new SimpleDateFormat(format);
                } else {
                    simpleDateFormat = new SimpleDateFormat(format, locale);
                }
                try {
                    return simpleDateFormat.parse((String) attribute);
                } catch (ParseException e) {
                    logger.error("parse exception on " + attribute + " with format : " + format, e);
                }
            } else if (attribute instanceof Integer) {
                return new Date((Integer) attribute);
            } else if (attribute instanceof Long) {
                return new Date((Long) attribute);
            }
        }
        return null;
    }
}
