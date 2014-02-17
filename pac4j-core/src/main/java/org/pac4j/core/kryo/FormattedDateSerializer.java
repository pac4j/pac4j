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
package org.pac4j.core.kryo;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Locale;

import org.pac4j.core.profile.FormattedDate;

import com.esotericsoftware.kryo.serialize.LongSerializer;
import com.esotericsoftware.kryo.serialize.SimpleSerializer;
import com.esotericsoftware.kryo.serialize.StringSerializer;

/**
 * This class is a Kryo serializer for {@link FormattedDate}.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class FormattedDateSerializer extends SimpleSerializer<FormattedDate> {
    
    private final LongSerializer longSerializer = new LongSerializer();
    
    private final StringSerializer stringSerializer = new StringSerializer();
    
    private final LocaleSerializer localeSerializer = new LocaleSerializer();
    
    @Override
    public FormattedDate read(final ByteBuffer buffer) {
        final Long time = this.longSerializer.readObject(buffer, Long.class);
        final String format = this.stringSerializer.readObject(buffer, String.class);
        final Locale locale = this.localeSerializer.readObject(buffer, Locale.class);
        return new FormattedDate(new Date(time), format, locale);
    }
    
    @Override
    public void write(final ByteBuffer buffer, final FormattedDate object) {
        this.longSerializer.writeObject(buffer, object.getTime());
        this.stringSerializer.writeObject(buffer, object.getFormat());
        this.localeSerializer.writeObject(buffer, object.getLocale());
    }
}
