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

    //Digest components
    String DIGEST_RESPONSE = "0353b452a373c2bf9dbff4e0abaf3be7";
    String REALM = "testRealm";
    String NONCE = "a19574258e80cb0833c58819e009303e";
    String URI = "/api/users/list?accountid=testaccount";
    String NC = "00000001";
    String QOP = "auth";
    String CNONCE = "ICAgICAgICAgICAgICAgICAgICAgICAgICA1NzI2NzA=";
    String DIGEST_AUTHORIZATION_HEADER_VALUE = "Digest username=\"" + USERNAME + ",realm=\"" + REALM + "\",nonce=\"" + NONCE + "\"," +
            "uri=\"" + URI + "\",response=\"" + DIGEST_RESPONSE + "\",qop=\"" + QOP + "\",nc=\"" + NC + "\",cnonce=\"" + CNONCE + "\"";

    // urls
    String CALLBACK_URL = "http://myserver/callback";
    String GOOGLE_URL = "http://www.google.com";
    String LOGIN_URL = "http://myserver/login";
    String PAC4J_BASE_URL = "http://www.pac4j.org/";
    String PAC4J_URL = PAC4J_BASE_URL + "test.html";
}
