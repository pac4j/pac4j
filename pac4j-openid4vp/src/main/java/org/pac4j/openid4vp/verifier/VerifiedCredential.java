package org.pac4j.openid4vp.verifier;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.pac4j.openid4vp.config.CredentialFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * One credential, once its signatures, its key binding and its issuer have been validated.
 * Everything past this point works on claims and no longer on formats.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class VerifiedCredential implements Serializable {

    @Serial
    private static final long serialVersionUID = -1055834287963612504L;

    private CredentialFormat format;

    /** The credential type: the {@code vct} for SD-JWT VC, the doctype for a mobile document. */
    private String type;

    private String issuer;

    /** The claims actually disclosed by the holder. */
    private Map<String, Object> claims = new LinkedHashMap<>();
}
