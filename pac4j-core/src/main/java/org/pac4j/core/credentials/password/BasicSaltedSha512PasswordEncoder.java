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
package org.pac4j.core.credentials.password;

import org.apache.commons.codec.digest.DigestUtils;
import org.pac4j.core.util.CommonHelper;

/**
 * A password encoder based on SHA512 and using a salt.
 *
 * Add the <code>commons-codec</code> dependency to use this class.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class BasicSaltedSha512PasswordEncoder implements PasswordEncoder {

    protected String salt;

    public BasicSaltedSha512PasswordEncoder() {
    }

    public BasicSaltedSha512PasswordEncoder(final String salt) {
        this.salt = salt;
    }

    @Override
    public String encode(final String password) {
        CommonHelper.assertNotBlank("salt", salt);

        return DigestUtils.sha512Hex(password + salt);
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
