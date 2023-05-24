package org.pac4j.core.credentials;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;

/**
 * This credentials represents a token.
 *
 * @author Jerome Leleu
 * @since 1.7.0
 */
@EqualsAndHashCode(callSuper = false)
@ToString
@AllArgsConstructor
public class TokenCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = -4270718634364817595L;

    @Getter
    private String token;
}
