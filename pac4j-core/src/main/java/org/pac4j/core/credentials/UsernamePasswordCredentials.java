package org.pac4j.core.credentials;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

/**
 * This class represents a username and a password credentials
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@ToString(exclude = "password")
public class UsernamePasswordCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = -7229878989627796565L;

    @Getter
    private String username;

    @Getter
    private String password;
}
