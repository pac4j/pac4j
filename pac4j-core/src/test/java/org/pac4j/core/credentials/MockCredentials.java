package org.pac4j.core.credentials;

/**
 * Mock a credentials.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class MockCredentials extends AuthenticationCredentials {

    private static final long serialVersionUID = 7137723104269576820L;

    @Override
    public boolean equals(final Object o) {
        return o instanceof MockCredentials;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
