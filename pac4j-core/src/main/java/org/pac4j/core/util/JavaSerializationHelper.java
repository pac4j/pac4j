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
package org.pac4j.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.*;

/**
 * Helper for Java serialization.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class JavaSerializationHelper {

    private static final Logger logger = LoggerFactory.getLogger(JavaSerializationHelper.class);

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
        byte[] bytes = null;
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            oos.flush();
            bytes = baos.toByteArray();
        } catch (final IOException e) {
            logger.warn("cannot Java serialize object", e);
        }
        return bytes;
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
        Serializable o = null;
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final ObjectInputStream ois = new ObjectInputStream(bais)) {
            o = (Serializable) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            logger.warn("cannot Java deserialize object", e);
        }
        return o;
    }
}
