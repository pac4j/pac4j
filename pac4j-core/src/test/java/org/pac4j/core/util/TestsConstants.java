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
package org.pac4j.core.util;

/**
 * This class has all the constants for tests.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface TestsConstants {
    
    // simple objects
    String VALUE = "value";
    int MILLIS_BETWEEN_CLEANUPS = 30000;
    String TYPE = "type";
    String SCOPE = "scope";
    String FIELDS = "fields";
    int LIMIT = 112;
    String REALM_NAME = "realmName";
    String HEADER_NAME = "headerName";
    String PREFIX_HEADER = "prefixHeader";
    String USERNAME = "username";
    String PASSWORD = "password";
    String STRING_ID = "id";
    String KEY = "key";
    String PARAMETER_NAME = "parameterName";
    String FAKE_VALUE = "fakeValue";
    String SECRET = "secret";
    String TOKEN = "token";
    String VERIFIER = "verifier";
    String CODE = "code";
    String ELEMENT = "element";
    String ELEMENT2 = "element2";
    String NAME = "name";
    String BAD_JSON = "{ }";
    String TITLE = "title";
    String NAMESPACE = "namespace";
    int INT_ID = 1234;
    String SALT = "sel";
    String GOOD_USERNAME = "jle";
    String GOOD_USERNAME2 = "jleleu";
    String BAD_USERNAME = "michael";
    String MULTIPLE_USERNAME = "misagh";
    String FIRSTNAME = "firstname";
    String FIRSTNAME_VALUE = "Jerome";
    String CLIENT_NAME = "clientname";
    String ROLE = "role";

    // urls
    String CALLBACK_URL = "http://myserver/callback";
    String GOOGLE_URL = "http://www.google.com";
    String LOGIN_URL = "http://myserver/login";
    String PAC4J_BASE_URL = "http://www.pac4j.org/";
    String PAC4J_URL = PAC4J_BASE_URL + "test.html";
}
