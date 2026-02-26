package org.pac4j.core.keystore.generation;

import java.io.InputStream;

/**
 * This is {@link KeystoreGenerator}.
 *
 * @author Jérôme LELEU
 * @since 6.4.0
 */
public interface KeystoreGenerator {

    /**
     * <p>generate.</p>
     */
    void generate();

    /**
     * <p>shouldGenerate.</p>
     *
     * @return a boolean
     */
    boolean shouldGenerate();

    /**
     * <p>retrieve.</p>
     *
     * @return a {@link InputStream} object
     * @throws Exception if any.
     */
    InputStream retrieve() throws Exception;

}
