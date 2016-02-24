package org.pac4j.core.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.*;

import java.util.Locale;

/**
 * This class is a Kryo serializer for {@link Locale}.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class LocaleSerializer extends Serializer<Locale> {

    private final StringSerializer stringSerializer = new StringSerializer();

    @Override
    public Locale read(Kryo kryo, Input input, Class<Locale> aClass) {
        final String language = this.stringSerializer.read(kryo, input, String.class);
        final String country = this.stringSerializer.read(kryo, input, String.class);
        final String variant = this.stringSerializer.read(kryo, input, String.class);
        if (language == null && country == null && variant == null) {
            return null;
        } else {
            return new Locale(language, country, variant);
        }
    }

    @Override
    public void write(Kryo kryo, Output output, Locale locale) {
        if (locale != null) {
            this.stringSerializer.write(kryo, output, locale.getLanguage());
            this.stringSerializer.write(kryo, output, locale.getCountry());
            this.stringSerializer.write(kryo, output, locale.getVariant());
        } else {
            this.stringSerializer.write(kryo, output, null);
            this.stringSerializer.write(kryo, output, null);
            this.stringSerializer.write(kryo, output, null);
        }
    }
}
