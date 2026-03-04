package org.pac4j.core.keystore.loading;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.keystore.generation.KeystoreGenerator;
import org.pac4j.core.util.TestsConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link KeyStoreUtils}.
 *
 * @author Jerome Leleu
 * @since 6.4.0
 */
public final class KeyStoreUtilsTests implements TestsConstants {

    private KeyStore prepareKeyStore() throws Exception {
        return prepareKeyStore(false, true);
    }

    private KeyStore prepareKeyStore(final boolean keyIsPrivate, final boolean valueIsPrivate) throws Exception {
        val keyStoreSpiMock = mock(KeyStoreSpi.class);
        val keyStore = new KeyStore(keyStoreSpiMock, null, "test"){ };
        keyStore.load(null);
        when(keyStore.aliases()).thenReturn(Collections.enumeration(Arrays.asList(KEY, VALUE)));
        when(keyStore.entryInstanceOf(KEY, KeyStore.PrivateKeyEntry.class)).thenReturn(keyIsPrivate);
        when(keyStore.entryInstanceOf(VALUE, KeyStore.PrivateKeyEntry.class)).thenReturn(valueIsPrivate);
        return keyStore;
    }

    private byte[] buildEmptyJks(final String password) throws Exception {
        val keystore = KeyStore.getInstance(KeyStoreUtils.DEFAULT_KEYSTORE_TYPE);
        keystore.load(null, password.toCharArray());
        try (var os = new ByteArrayOutputStream()) {
            keystore.store(os, password.toCharArray());
            return os.toByteArray();
        }
    }

    @Test
    public void testReturnFirstAliasWhenNoKeystoreAlias() throws Exception {
        val keyStore = prepareKeyStore();
        assertEquals(VALUE, KeyStoreUtils.findPrivateKeyAlias(keyStore, null));
    }

    @Test
    public void testReturnMatchingAlias() throws Exception {
        val keyStore = prepareKeyStore();
        assertEquals(VALUE, KeyStoreUtils.findPrivateKeyAlias(keyStore, VALUE.toLowerCase()));
    }

    @Test
    public void testNoMatchingAliasThrowsException() throws Exception {
        val keyStore = prepareKeyStore();
        val exception = assertThrows(TechnicalException.class, () -> KeyStoreUtils.findPrivateKeyAlias(keyStore, ID));
        assertEquals("Keystore has no private keys to match the requested key alias " + ID, exception.getMessage());
    }

    @Test
    public void testNoPrivateKeyThrowsException() throws Exception {
        val keyStore = prepareKeyStore(false, false);
        val exception = assertThrows(TechnicalException.class, () -> KeyStoreUtils.findPrivateKeyAlias(keyStore, null));
        assertEquals("Keystore has no private keys to match the requested key alias null", exception.getMessage());
    }

    @Test
    public void testAliasesLookupFailureThrowsException() throws Exception {
        val keyStoreSpiMock = mock(KeyStoreSpi.class);
        val keyStore = new KeyStore(keyStoreSpiMock, null, "test"){ };
        keyStore.load(null);
        when(keyStore.aliases()).thenThrow(new IllegalStateException("boom"));

        val exception = assertThrows(TechnicalException.class, () -> KeyStoreUtils.findPrivateKeyAlias(keyStore, null));
        assertEquals("Unable to get aliases from keyStore", exception.getMessage());
        assertNotNull(exception.getCause());
    }

    @Test
    public void testLoadKeyStoreFromValidJks() throws Exception {
        val jksBytes = buildEmptyJks(PASSWORD);
        val loaded = KeyStoreUtils.loadKeyStore(new ByteArrayInputStream(jksBytes), PASSWORD, KeyStoreUtils.DEFAULT_KEYSTORE_TYPE);
        assertEquals(0, loaded.size());
        assertEquals(KeyStoreUtils.DEFAULT_KEYSTORE_TYPE, loaded.getType());
    }

    @Test
    public void testLoadKeyStoreWithInvalidTypeThrowsException() {
        val exception = assertThrows(TechnicalException.class,
            () -> KeyStoreUtils.loadKeyStore(new ByteArrayInputStream(new byte[0]), PASSWORD, "UNSUPPORTED_TYPE"));
        assertEquals("Error loading keystore", exception.getMessage());
    }

    @Test
    public void testRetrieveKeyStoreAndAliasValidatesKeystorePassword() {
        val properties = new KeystoreProperties();
        properties.setPrivateKeyPassword(PASSWORD);

        val exception = assertThrows(TechnicalException.class, () -> KeyStoreUtils.retrieveKeyStoreAndAlias(properties));
        assertEquals("keystorePassword cannot be blank", exception.getMessage());
    }

    @Test
    public void testRetrieveKeyStoreAndAliasValidatesPrivateKeyPassword() {
        val properties = new KeystoreProperties();
        properties.setKeystorePassword(PASSWORD);

        val exception = assertThrows(TechnicalException.class, () -> KeyStoreUtils.retrieveKeyStoreAndAlias(properties));
        assertEquals("privateKeyPassword cannot be blank", exception.getMessage());
    }

    @Test
    public void testRetrieveKeyStoreAndAliasWrapsGeneratorFailure() throws Exception {
        val properties = new KeystoreProperties();
        properties.setKeystorePassword(PASSWORD);
        properties.setPrivateKeyPassword(PASSWORD);
        val generator = mock(KeystoreGenerator.class);
        when(generator.retrieve()).thenThrow(new IllegalStateException("boom"));
        properties.setKeystoreGenerator(generator);

        val exception = assertThrows(TechnicalException.class, () -> KeyStoreUtils.retrieveKeyStoreAndAlias(properties));
        assertNotNull(exception.getCause());
        assertEquals(IllegalStateException.class, exception.getCause().getClass());
        assertEquals("boom", exception.getCause().getMessage());
    }
}
