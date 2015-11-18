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
package org.pac4j.saml.crypto;

import java.io.ByteArrayInputStream;

import org.opensaml.security.credential.Credential;


/**
 * Class responsible for loading a private key from a JKS keystore and returning the corresponding {@link Credential}
 * opensaml object.
 * 
 * The key store is loaded from a supplied binary array, which should contain a complete JKS image.
 *
 * @author jkacer
 * @since 1.9.0
 */
public class KeyStoreByteArrayCredentialProvider extends KeyStoreCredentialProvider { 

	/**
	 * Creates a key store credential provider, based on a byte array.
	 * 
	 * @param keystoreBinData
	 *            Binary data of a JKS keystore.
	 * @param storePasswd
	 *            Password to the key store.
	 * @param privateKeyPasswd
	 *            Password to a single entry inside the key store.
	 */
    public KeyStoreByteArrayCredentialProvider(final byte[] keystoreBinData, final String storePasswd, final String privateKeyPasswd) {
    	super(new ByteArrayInputStream(keystoreBinData),storePasswd,privateKeyPasswd);
    }
    
}
