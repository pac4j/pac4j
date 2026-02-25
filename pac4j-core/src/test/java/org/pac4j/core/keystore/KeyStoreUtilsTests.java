package org.pac4j.core.keystore;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.util.TestsConstants;

import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        val keyStoreSpiMock = mock(KeyStoreSpi.class);
        val keyStore = new KeyStore(keyStoreSpiMock, null, "test"){ };
        keyStore.load(null);
        when(keyStore.aliases()).thenReturn(Collections.enumeration(Arrays.asList(KEY, VALUE)));
        when(keyStore.entryInstanceOf(KEY, KeyStore.PrivateKeyEntry.class)).thenReturn(false);
        when(keyStore.entryInstanceOf(VALUE, KeyStore.PrivateKeyEntry.class)).thenReturn(true);
        return keyStore;
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
}
