package org.pac4j.core.util.serializer;

import org.pac4j.core.util.JavaSerializationHelper;

/**
 * A serializer for the {@link org.pac4j.core.profile.service.ProfileService}.
 * It reads JSON or Java serialized data and writes in JSON.
 *
 * @author Jerome LELEU
 * @since 3.9.0
 */
public class ProfileServiceSerializer implements Serializer {

    private JsonSerializer jsonSerializer;

    private JavaSerializationHelper javaSerializationHelper;

    public ProfileServiceSerializer(final Class<? extends Object> clazz) {
        this(new JsonSerializer(clazz), new JavaSerializationHelper());
    }

    public ProfileServiceSerializer(final JsonSerializer jsonSerializer, final JavaSerializationHelper javaSerializationHelper) {
        this.jsonSerializer = jsonSerializer;
        this.javaSerializationHelper = javaSerializationHelper;
    }

    @Override
    public String encode(final Object obj) {
        return jsonSerializer.encode(obj);
    }

    @Override
    public Object decode(final String encoded) {
        if (encoded == null) {
            return null;
        }
        if (encoded.startsWith("{")) {
            return jsonSerializer.decode(encoded);
        } else {
            return javaSerializationHelper.deserializeFromBase64(encoded);
        }
    }
}
