package org.pac4j.saml.crypto;

import lombok.val;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests {@link KeyStoreCredentialProvider}.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public final class KeyStoreCredentialProviderTests implements TestsConstants {

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
        assertEquals(VALUE, KeyStoreCredentialProvider.getPrivateKeyAlias(keyStore, null));
    }

    @Test
    public void testReturnMatchingAlias() throws Exception {
        val keyStore = prepareKeyStore();
        assertEquals(VALUE, KeyStoreCredentialProvider.getPrivateKeyAlias(keyStore, VALUE.toLowerCase()));
    }
}
