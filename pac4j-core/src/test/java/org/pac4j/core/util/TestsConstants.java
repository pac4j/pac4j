package org.pac4j.core.util;

/**
 * This class has all the constants for tests.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public interface TestsConstants {
    
    // simple objects
    String ID = "id";
    String VALUE = "value";
    String TYPE = "type";
    String HEADER_NAME = "headerName";
    String PREFIX_HEADER = "prefixHeader";
    String USERNAME = "username";
    String PASSWORD = "password";
    String KEY = "key";
    String FAKE_VALUE = "fakeValue";
    String SECRET = "secret";
    String TOKEN = "token";
    String VERIFIER = "verifier";
    String CODE = "code";
    String NAME = "name";
    String SALT = "salt";
    String GOOD_USERNAME = "jle";
    String GOOD_USERNAME2 = "jleleu";
    String BAD_USERNAME = "michael";
    String MULTIPLE_USERNAME = "misagh";
    String FIRSTNAME = "firstname";
    String FIRSTNAME_VALUE = "Jerome";
    String CLIENT_NAME = "clientname";
    String ROLE = "role";
    String PATH = "/tmp/path";
    String EMAIL = "test@example.com";

    // urls
    String CALLBACK_URL = "http://myserver/callback";
    String GOOGLE_URL = "http://www.google.com";
    String LOGIN_URL = "http://myserver/login";
    String PAC4J_BASE_URL = "http://www.pac4j.org/";
    String PAC4J_URL = PAC4J_BASE_URL + "test.html";
}
