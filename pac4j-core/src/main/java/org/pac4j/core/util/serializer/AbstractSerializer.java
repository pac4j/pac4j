package org.pac4j.core.util.serializer;

import lombok.val;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * The abstract implementation for all serializers: check for nulls and convert strings from or to bytes arrays.
 *
 * @author Jerome LELEU
 * @since 5.0.0
 */
public abstract class AbstractSerializer implements Serializer {

    /** {@inheritDoc} */
    @Override
    public final String serializeToString(final Object obj) {
        if (obj == null) {
            return null;
        }

        return internalSerializeToString(obj);
    }

    /**
     * <p>internalSerializeToString.</p>
     *
     * @param obj a {@link java.lang.Object} object
     * @return a {@link java.lang.String} object
     */
    protected String internalSerializeToString(final Object obj) {
        return Base64.getEncoder().encodeToString(internalSerializeToBytes(obj));
    }

    /** {@inheritDoc} */
    @Override
    public final byte[] serializeToBytes(final Object obj) {
        if (obj == null) {
            return null;
        }

        return internalSerializeToBytes(obj);
    }

    /**
     * <p>internalSerializeToBytes.</p>
     *
     * @param obj a {@link java.lang.Object} object
     * @return an array of {@link byte} objects
     */
    protected byte[] internalSerializeToBytes(final Object obj) {
        return internalSerializeToString(obj).getBytes(StandardCharsets.UTF_8);
    }

    /** {@inheritDoc} */
    @Override
    public final Object deserializeFromString(final String encoded) {
        if (encoded == null) {
            return null;
        }

        return internalDeserializeFromString(encoded);
    }

    /**
     * <p>internalDeserializeFromString.</p>
     *
     * @param encoded a {@link java.lang.String} object
     * @return a {@link java.lang.Object} object
     */
    protected Object internalDeserializeFromString(final String encoded) {
        val enc = Base64.getDecoder().decode(encoded);
        return internalDeserializeFromBytes(enc);
    }

    /** {@inheritDoc} */
    @Override
    public final Object deserializeFromBytes(final byte[] encoded) {
        if (encoded == null) {
            return null;
        }

        return internalDeserializeFromBytes(encoded);
    }

    /**
     * <p>internalDeserializeFromBytes.</p>
     *
     * @param encoded an array of {@link byte} objects
     * @return a {@link java.lang.Object} object
     */
    protected Object internalDeserializeFromBytes(final byte[] encoded) {
        return internalDeserializeFromString(new String(encoded, StandardCharsets.UTF_8));
    }
}
