package org.pac4j.core.util.serializer;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The abstract implementation for all serializers: check for nulls and convert strings from or to bytes arrays.
 *
 * @author Jerome LELEU
 * @since 5.0.0
 */
public abstract class AbstractSerializer implements Serializer {

    @Override
    public final String serializeToString(final Object obj) {
        if (obj == null) {
            return null;
        }

        return internalSerializeToString(obj);
    }

    protected String internalSerializeToString(final Object obj) {
        return Base64.getEncoder().encodeToString(internalSerializeToBytes(obj));
    }

    @Override
    public final byte[] serializeToBytes(final Object obj) {
        if (obj == null) {
            return null;
        }

        return internalSerializeToBytes(obj);
    }

    protected byte[] internalSerializeToBytes(final Object obj) {
        return internalSerializeToString(obj).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public final Object deserializeFromString(final String encoded) {
        if (encoded == null) {
            return null;
        }

        return internalDeserializeFromString(encoded);
    }

    protected Object internalDeserializeFromString(final String encoded) {
        final byte[] enc = Base64.getDecoder().decode(encoded);
        return internalDeserializeFromBytes(enc);
    }

    @Override
    public final Object deserializeFromBytes(final byte[] encoded) {
        if (encoded == null) {
            return null;
        }

        return internalDeserializeFromBytes(encoded);
    }

    protected Object internalDeserializeFromBytes(final byte[] encoded) {
        return internalDeserializeFromString(new String(encoded, StandardCharsets.UTF_8));
    }
}
