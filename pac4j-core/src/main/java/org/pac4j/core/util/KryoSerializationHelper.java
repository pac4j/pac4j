package org.pac4j.core.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * Helper for Kryo serialization.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class KryoSerializationHelper {

    private final Kryo kryo;

    public KryoSerializationHelper() {
        this.kryo = new Kryo();
    }

    public KryoSerializationHelper(final Kryo kryo) {
        this.kryo = kryo;
    }

    /**
     * Serialize a Java object into a base64 String.
     *
     * @param o the object to serialize
     * @return the base64 string of the serialized object
     */
    public String serializeToBase64(final Serializable o) {
        return DatatypeConverter.printBase64Binary(serializeToBytes(o));
    }

    /**
     * Serialize a Java object into a bytes array.
     *
     * @param o the object to serialize
     * @return the bytes array of the serialized object
     */
    public byte[] serializeToBytes(final Serializable o) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try (final Output output = new Output(byteStream)) {
            kryo.writeClassAndObject(output, o);
            output.flush();
            return byteStream.toByteArray();
        }
    }

    /**
     * Unserialize a base64 String into a Java object.
     *
     * @param base64 the serialized object as a base64 String
     * @return the unserialized Java object
     */
    public Serializable unserializeFromBase64(final String base64) {
        return unserializeFromBytes(DatatypeConverter.parseBase64Binary(base64));
    }

    /**
     * Unserialize a bytes array into a Java object.
     *
     * @param bytes the serialized object as a bytes array
     * @return the unserialized Java object
     */
    public Serializable unserializeFromBytes(final byte[] bytes) {
        try (final Input input = new Input(new ByteArrayInputStream(bytes))) {
            final Serializable obj =  (Serializable) kryo.readClassAndObject(input);
            return obj;
        }
    }
}
