package org.pac4j.saml.crypto;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests {@link KeyStoreCredentialProvider}.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public final class KeyStoreCredentialProviderTests implements TestsConstants {

    private KeyStore prepareKeyStore() throws Exception {
        final KeyStoreSpi keyStoreSpiMock = mock(KeyStoreSpi.class);
        final KeyStore keyStore = new KeyStore(keyStoreSpiMock, null, "test"){ };
        keyStore.load(null);
        when(keyStore.aliases()).thenReturn(Collections.enumeration(Arrays.asList(KEY, VALUE)));
        when(keyStore.entryInstanceOf(KEY, KeyStore.PrivateKeyEntry.class)).thenReturn(false);
        when(keyStore.entryInstanceOf(VALUE, KeyStore.PrivateKeyEntry.class)).thenReturn(true);
        return keyStore;
    }

    @Test
    public void testReturnFirstAliasWhenNoKeystoreAlias() throws Exception {
        final KeyStore keyStore = prepareKeyStore();
        assertEquals(VALUE, KeyStoreCredentialProvider.getPrivateKeyAlias(keyStore, null));
    }

    @Test
    public void testReturnMatchingAlias() throws Exception {
        final KeyStore keyStore = prepareKeyStore();
        assertEquals(VALUE, KeyStoreCredentialProvider.getPrivateKeyAlias(keyStore, VALUE.toLowerCase()));
    }
}
