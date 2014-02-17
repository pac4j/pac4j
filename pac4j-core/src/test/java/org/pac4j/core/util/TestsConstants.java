/*
  Copyright 2012 - 2014 Jerome Leleu

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
    public static final String VALUE = "value";
    public static final int MILLIS_BETWEEN_CLEANUPS = 30000;
    public static final String TYPE = "type";
    public static final String SCOPE = "scope";
    public static final String FIELDS = "fields";
    public static final int LIMIT = 112;
    public static final String REALM_NAME = "realmName";
    public final static String USERNAME = "username";
    public final static String PASSWORD = "password";
    public static final String STRING_ID = "id";
    public static final String KEY = "key";
    public static final String PARAMETER_NAME = "parameterName";
    public static final String FAKE_VALUE = "fakeValue";
    public static final String SECRET = "secret";
    public final static String TOKEN = "token";
    public final static String VERIFIER = "verifier";
    public static final String CODE = "code";
    public final static String ELEMENT = "element";
    public final static String ELEMENT2 = "element2";
    public static final String NAME = "name";
    public static final String BAD_JSON = "{ }";
    public static final String TITLE = "title";
    public static final String NAMESPACE = "namespace";
    public static final int INT_ID = 1234;
    
    // urls
    public static final String CALLBACK_URL = "http://myserver/callback";
    public static final String GOOGLE_URL = "http://www.google.com";
    public final static String LOGIN_URL = "http://myserver/login";
    public static final String PAC4J_BASE_URL = "http://www.pac4j.org/";
    public static final String PAC4J_URL = PAC4J_BASE_URL + "test.html";
}
