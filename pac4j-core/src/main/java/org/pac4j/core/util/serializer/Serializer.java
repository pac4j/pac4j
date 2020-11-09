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
    String encode(Object obj);

    /**
     * Decode a string into an object.
     *
     * @param encoded the encoded string
     * @return the decoded object
     */
    Object decode(String encoded);
}
