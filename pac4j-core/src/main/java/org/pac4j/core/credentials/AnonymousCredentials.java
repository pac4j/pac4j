package org.pac4j.core.credentials;

/**
 * Anonymous credentials. Not to be used except for advanced use cases.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class AnonymousCredentials extends Credentials {

    private static final long serialVersionUID = 7526472295622776147L;

    @Override
    public boolean equals(Object o) {
        return true;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
