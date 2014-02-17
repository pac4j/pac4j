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
import java.util.Locale;

import com.esotericsoftware.kryo.serialize.SimpleSerializer;
import com.esotericsoftware.kryo.serialize.StringSerializer;

/**
 * This class is a Kryo serializer for {@link Locale}.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class LocaleSerializer extends SimpleSerializer<Locale> {
    
    private final StringSerializer stringSerializer = new StringSerializer();
    
    @Override
    public Locale read(final ByteBuffer buffer) {
        final String language = this.stringSerializer.readObject(buffer, String.class);
        final String country = this.stringSerializer.readObject(buffer, String.class);
        final String variant = this.stringSerializer.readObject(buffer, String.class);
        return new Locale(language, country, variant);
    }
    
    @Override
    public void write(final ByteBuffer buffer, final Locale object) {
        this.stringSerializer.writeObject(buffer, object.getLanguage());
        this.stringSerializer.writeObject(buffer, object.getCountry());
        this.stringSerializer.writeObject(buffer, object.getVariant());
    }
}
