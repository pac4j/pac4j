package org.pac4j.oidc.exceptions;

/**
 * When an error occured for the user info call.
 *
 * @author Jerome LELEU
 * @since 5.7.2
 */
public class UserInfoErrorResponseException extends Exception {

    public static final UserInfoErrorResponseException INSTANCE = new UserInfoErrorResponseException();

    private UserInfoErrorResponseException() {}
}
