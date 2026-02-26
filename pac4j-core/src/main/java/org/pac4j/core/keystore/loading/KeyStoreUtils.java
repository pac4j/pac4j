package org.pac4j.core.keystore.loading;

import lombok.val;
import org.pac4j.core.exception.TechnicalException;

import java.io.InputStream;
import java.security.KeyStore;

/**
 * Utilities for dealing with {@link KeyStore} instances.
 *
 * @author Jerome Leleu
 * @since 6.4.0
 */
public final class KeyStoreUtils {

    public static KeyStore loadKeyStore(final InputStream inputStream, final String storePassword, final String keyStoreType) {
        try {
            val ks = KeyStore.getInstance(keyStoreType);
            ks.load(inputStream, storePassword == null ? null : storePassword.toCharArray());
            return ks;
        } catch (final Exception e) {
            throw new TechnicalException("Error loading keystore", e);
        }
    }

    /**
     * Find the private key alias to use.
     * <ul>
     *     <li>If {@code keyStoreAlias} is defined, return the matching alias (case-insensitive).</li>
     *     <li>Otherwise, return the first alias representing a {@link java.security.KeyStore.PrivateKeyEntry}.</li>
     * </ul>
     *
     * @param keyStore the keystore
     * @param keyStoreAlias the configured alias (optional)
     * @return the effective alias
     */
    public static String findPrivateKeyAlias(final KeyStore keyStore, final String keyStoreAlias) {
        try {
            val aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                val currentAlias = aliases.nextElement();
                if (keyStoreAlias != null) {
                    if (currentAlias.equalsIgnoreCase(keyStoreAlias)) {
                        return currentAlias;
                    }
                } else if (keyStore.entryInstanceOf(currentAlias, KeyStore.PrivateKeyEntry.class)) {
                    return currentAlias;
                }
            }
        } catch (final Exception e) {
            throw new TechnicalException("Unable to get aliases from keyStore", e);
        }
        throw new TechnicalException("Keystore has no private keys to match the requested key alias " + keyStoreAlias);
    }
}
