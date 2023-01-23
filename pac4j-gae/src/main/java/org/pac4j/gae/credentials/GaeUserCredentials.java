package org.pac4j.gae.credentials;

import com.google.appengine.api.users.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.credentials.AuthenticationCredentials;

/**
 * Credential for Google App Engine.
 *
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GaeUserCredentials extends AuthenticationCredentials {

    private static final long serialVersionUID = -135519596194113906L;

    private User user;
}
