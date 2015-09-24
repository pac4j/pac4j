/*
  Copyright 2012 - 2015 pac4j organization

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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.*;
import org.pac4j.core.profile.FormattedDate;

import java.util.Date;
import java.util.Locale;

/**
 * This class is a Kryo serializer for {@link FormattedDate}.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class FormattedDateSerializer extends Serializer<FormattedDate> {

    private final LongSerializer longSerializer = new LongSerializer();

    private final StringSerializer stringSerializer = new StringSerializer();

    private final LocaleSerializer localeSerializer = new LocaleSerializer();

    @Override
    public FormattedDate read(Kryo kryo, Input input, Class<FormattedDate> aClass) {
        final Long time = this.longSerializer.read(kryo, input, Long.class);
        final String format = this.stringSerializer.read(kryo, input, String.class);
        final Locale locale = this.localeSerializer.read(kryo, input, Locale.class);
        return new FormattedDate(new Date(time), format, locale);
    }

    @Override
    public void write(Kryo kryo, Output output, FormattedDate formattedDate) {
        this.longSerializer.write(kryo, output, formattedDate.getTime());
        this.stringSerializer.write(kryo, output, formattedDate.getFormat());
        this.localeSerializer.write(kryo, output, formattedDate.getLocale());
    }
}
