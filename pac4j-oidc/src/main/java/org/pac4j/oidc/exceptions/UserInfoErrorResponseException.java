package org.pac4j.oidc.exceptions;

/**
 * When an error occured for the user info call.
 *
 * @author Jerome LELEU
 * @since 5.7.2
 */
public class UserInfoErrorResponseException extends Exception {

    public UserInfoErrorResponseException(final String message) {
        super("Cannot retrieve user info: " + message);
    }
}
