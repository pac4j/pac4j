package org.pac4j.core.util.serializer;

/**
 * The serializer contract.
 *
 * @author Jerome LELEU
 * @since 3.9.0
 */
public interface Serializer {

    /**
     * Encode an object into a string.
     *
     * @param obj the object to encode
     * @return the encoded string
     */
    String serializeToString(Object obj);

    /**
     * Encode an object into a bytes array.
     *
     * @param obj the object to encode
     * @return the encoded bytes array
     */
    byte[] serializeToBytes(Object obj);

    /**
     * Decode a string into an object.
     *
     * @param encoded the encoded string
     * @return the decoded object
     */
    Object deserializeFromString(String encoded);

    /**
     * Decode a bytes array into an object.
     *
     * @param encoded the encoded bytes array
     * @return the decoded object
     */
    Object deserializeFromBytes(byte[] encoded);
}
