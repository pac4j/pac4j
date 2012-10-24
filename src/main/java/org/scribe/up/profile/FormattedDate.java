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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents a formatted date.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FormattedDate extends Date {
    
    private static final long serialVersionUID = -279874272436563464L;
    
    private String format;
    
    private Locale locale;
    
    public FormattedDate() {
    }
    
    public FormattedDate(final Date date, final String format, final Locale locale) {
        super(date.getTime());
        this.format = format;
        this.locale = locale;
    }
    
    public String getFormat() {
        return this.format;
    }
    
    public void setFormat(final String format) {
        this.format = format;
    }
    
    public void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat simpleDateFormat;
        if (locale == null) {
            simpleDateFormat = new SimpleDateFormat(format);
        } else {
            simpleDateFormat = new SimpleDateFormat(format, locale);
        }
        return simpleDateFormat.format(this);
    }
}
