package org.pac4j.core.util.serializer;

import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Java serializer.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class JavaSerializer extends AbstractSerializer {

    private static final Logger logger = LoggerFactory.getLogger(JavaSerializer.class);

    private Set<String> trustedPackages;

    private Set<Class<?>> trustedClasses;

    public JavaSerializer() {
        trustedPackages = new HashSet<>();
        trustedPackages.addAll(Arrays.asList("java.", "javax.", "[Ljava.lang.String", "org.pac4j.", "[Lorg.pac4j.",
                "com.github.scribejava.", "org.opensaml.", "com.nimbusds.", "[Lcom.nimbusds.", "org.joda.", "net.minidev.json.",
                "org.bson.types.", "[Ljava.lang.StackTraceElement", "[B"));
        trustedClasses = new HashSet<>();
    }

    /**
     * Serialize a Java object into a bytes array.
     *
     * @param o the object to serialize
     * @return the bytes array of the serialized object
     */
    @Override
    protected byte[] internalSerializeToBytes(final Object o) {
        byte[] bytes = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            oos.flush();
            bytes = baos.toByteArray();
        } catch (final IOException e) {
            logger.warn("cannot Java serialize object", e);
        }
        return bytes;
    }

    /**
     * Deserialize a bytes array into a Java object.
     *
     * @param bytes the serialized object as a bytes array
     * @return the deserialized Java object
     */
    @Override
    protected Serializable internalDeserializeFromBytes(final byte[] bytes) {
        Serializable o = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
             ObjectInputStream ois = new RestrictedObjectInputStream(bais, this.trustedPackages, this.trustedClasses)) {
            o = (Serializable) ois.readObject();
        } catch (final IOException | ClassNotFoundException e) {
            logger.warn("cannot Java deserialize object", e);
        }
        return o;
    }

    /**
     * Returns an immutable set of tusted packages.
     *
     * @return the trusted packages
     */
    public Set<String> getTrustedPackages() {
        return Collections.unmodifiableSet(trustedPackages);
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

        private final Set<String> trustedPackages;

        private final Map<String, Class<?>> trustedClasses; // className -> Class

        private RestrictedObjectInputStream(final InputStream in, final Set<String> trustedPackages,
                                            final Set<Class<?>> trustedClasses) throws IOException {
            super(in);
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
