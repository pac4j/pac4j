package org.pac4j.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Helper for Java serialization.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class JavaSerializationHelper {

    private static final Logger logger = LoggerFactory.getLogger(JavaSerializationHelper.class);

    private List<String> trustedPackages;

    private Set<Class<?>> trustedClasses;

    public JavaSerializationHelper() {
        trustedPackages = new ArrayList<>();
        trustedPackages.addAll(Arrays.asList("java.", "javax.", "org.pac4j.", "com.github.scribejava.", "org.opensaml.", "com.nimbusds.",
            "[Lcom.nimbusds.", "org.joda.", "net.minidev.json.", "org.bson.types."));
        trustedClasses = new HashSet<>();
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
            final ObjectInputStream ois = new RestrictedObjectInputStream(bais, this.trustedPackages, this.trustedClasses)) {
            o = (Serializable) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            logger.warn("cannot Java deserialize object", e);
        }
        return o;
    }

    /**
     * Returns a mutable list of trusted packages.
     *
     * @deprecated use {@link #addTrustedPackages} and {@link #addTrustedClasses}.
     * In later releases this method will return a copy of TrustedPackages with type {@link Set}.
     *
     * @return the trusted packages
     */
    public List<String> getTrustedPackages() {
        return trustedPackages;
    }

    /**
     * @deprecated use {@link #addTrustedPackages}, {@link #addTrustedPackage} and {@link #clearTrustedPackages}
     *
     * @param trustedPackages the trusted packages
     */
    @Deprecated
    public void setTrustedPackages(final List<String> trustedPackages) {
        this.trustedPackages = trustedPackages;
    }

    /**
     * Returns an immutable set of trusted classes.
     *
     * @return the trusted classes
     */
    public Set<Class<?>> getTrustedClasses() {
        return Collections.unmodifiableSet(trustedClasses);
    }

    public void addTrustedPackages(final Collection<String> trustedPackages) {
        this.trustedPackages.addAll(trustedPackages);
    }

    public void addTrustedPackage(final String trustedPackage) {
        this.trustedPackages.add(trustedPackage);
    }

    public void clearTrustedPackages() {
        this.trustedPackages.clear();
    }

    public void addTrustedClasses(final Collection<Class<?>> trustedClasses) {
        this.trustedClasses.addAll(trustedClasses);
    }

    public void addTrustedClass(final Class<?> trustedClass) {
        this.trustedClasses.add(trustedClass);
    }

    public void clearTrustedClasses() {
        this.trustedClasses.clear();
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "trustedPackages", this.trustedPackages, "trustedClasses", this.trustedClasses);
    }

    /**
     * Restricted <code>ObjectInputStream</code> for security reasons.
     */
    private static class RestrictedObjectInputStream extends ObjectInputStream {

        private final List<String> trustedPackages;

        private final Map<String, Class<?>> trustedClasses; // className -> Class

        private RestrictedObjectInputStream(final InputStream in, final List<String> trustedPackages,
                                            final Set<Class<?>> trustedClasses) throws IOException {
            super(in);
            CommonHelper.assertNotNull("trustedPackages", trustedPackages);
            this.trustedPackages = trustedPackages;
            this.trustedClasses = trustedClasses.stream().collect(Collectors.toMap(Class::getName, Function.identity()));
        }

        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            final String qualifiedClassName = desc.getName();
            final Class<?> clazz = trustedClasses.get(qualifiedClassName);
            if (Objects.nonNull(clazz)) {
                return clazz;
            } else if (trustedPackages.stream().anyMatch(qualifiedClassName::startsWith)) {
                return super.resolveClass(desc);
            } else {
                throw new ClassNotFoundException("Wont resolve untrusted class: " + qualifiedClassName);
            }
        }

        @Override
        protected Class<?> resolveProxyClass(String[] interfaces) throws ClassNotFoundException {
            throw new ClassNotFoundException("Wont resolve proxy classes at all.");
        }
    }
}
