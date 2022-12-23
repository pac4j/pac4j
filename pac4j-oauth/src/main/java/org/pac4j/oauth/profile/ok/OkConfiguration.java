package org.pac4j.oauth.profile.ok;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * Ok OAuth configuration.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
@ToString(callSuper = true)
@Getter
@Setter
@Accessors(chain = true)
@With
@AllArgsConstructor
@NoArgsConstructor
public class OkConfiguration extends OAuth20Configuration {

    /**
     * Public key (required as well as application key by API on ok.ru)
     */
    private String publicKey;
}
