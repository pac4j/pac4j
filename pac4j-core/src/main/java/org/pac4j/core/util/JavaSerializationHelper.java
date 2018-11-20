package org.pac4j.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Helper for Java serialization.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class JavaSerializationHelper {

    private static final Logger logger = LoggerFactory.getLogger(JavaSerializationHelper.class);

    private List<String> trustedPackages;

    public JavaSerializationHelper() {
        trustedPackages = new ArrayList<>();
        trustedPackages.addAll(Arrays.asList("java.", "javax.", "org.pac4j.", "com.github.scribejava.", "org.opensaml.", "com.nimbusds.",
            "[Lcom.nimbusds.", "org.joda.", "net.minidev.json.", "org.bson.types."));
    }

    /**
     * Serialize a Java object into a base64 String.
     *
     * @param o the object to serialize
     * @return the base64 string of the serialized object
     */
    public String serializeToBase64(final Serializable o) {
        return Base64.getEncoder().encodeToString(serializeToBytes(o));
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
        return unserializeFromBytes(Base64.getDecoder().decode(base64));
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
            final ObjectInputStream ois = new RestrictedObjectInputStream(bais, this.trustedPackages)) {
            o = (Serializable) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            logger.warn("cannot Java deserialize object", e);
        }
        return o;
    }

    public List<String> getTrustedPackages() {
        return trustedPackages;
    }

    public void setTrustedPackages(final List<String> trustedPackages) {
        this.trustedPackages = trustedPackages;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "trustedPackages", this.trustedPackages);
    }

    /**
     * Restricted <code>ObjectInputStream</code> for security reasons.
     */
    private static class RestrictedObjectInputStream extends ObjectInputStream {

        private final List<String> trustedPackages;

        private RestrictedObjectInputStream(final InputStream in, final List<String> trustedPackages) throws IOException {
            super(in);
            CommonHelper.assertNotNull("trustedPackages", trustedPackages);
            this.trustedPackages = trustedPackages;
        }

        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            final String qualifiedClassName = desc.getName();
            for (final String p : trustedPackages) {
                if (qualifiedClassName.startsWith(p)) {
                    return super.resolveClass(desc);
                }
            }
            throw new ClassNotFoundException("Wont resolve untrusted class: " + qualifiedClassName);
        }

        @Override
        protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
            throw new ClassNotFoundException("Wont resolve proxy classes at all.");
        }
    }
}
