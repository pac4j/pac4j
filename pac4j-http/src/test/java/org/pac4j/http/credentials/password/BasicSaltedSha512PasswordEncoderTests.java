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
package org.pac4j.http.credentials.password;

import org.junit.Test;
import org.pac4j.core.exception.TechnicalException;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the {@link BasicSaltedSha512PasswordEncoder}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class BasicSaltedSha512PasswordEncoderTests {

    private final static String PASSWORD = "password";
    private final static String SALTED_PASSWORD = "fa6a2185b3e0a9a85ef41ffb67ef3c1fb6f74980f8ebf970e4e72e353ed9537d593083c201dfd6e43e1c8a7aac2bc8dbb119c7dfb7d4b8f131111395bd70e97f";
    private final static String SALT = "salt";

    @Test(expected = TechnicalException.class)
    public void testNoSalt() {
        final PasswordEncoder encoder = new BasicSaltedSha512PasswordEncoder();
        encoder.encode(PASSWORD);
    }

    @Test
    public void testEncoding() {
        final PasswordEncoder encoder = new BasicSaltedSha512PasswordEncoder(SALT);
        final String encodedPwd = encoder.encode(PASSWORD);
        assertEquals(SALTED_PASSWORD, encodedPwd);
    }
}
