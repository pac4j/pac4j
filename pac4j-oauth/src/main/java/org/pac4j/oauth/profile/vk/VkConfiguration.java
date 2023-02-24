package org.pac4j.oauth.profile.vk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;
import org.pac4j.oauth.config.OAuth20Configuration;

/**
 * Vk OAuth configuration.
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
public class VkConfiguration extends OAuth20Configuration {

    /** Constant <code>DEFAULT_FIELDS="sex,bdate,photo_50,photo_100,photo_200_"{trunked}</code> */
    public final static String DEFAULT_FIELDS = "sex,bdate,photo_50,photo_100,photo_200_orig,photo_200,photo_400_orig,photo_max,"
        + "photo_max_orig,online,online_mobile,lists,domain,has_mobile,contacts,connections,site,education,can_post,can_see_all_posts,"
        + "can_see_audio,can_write_private_message,status,common_count,relation,relatives";

    protected String fields = DEFAULT_FIELDS;

    /** Constant <code>DEFAULT_SCOPE="PERMISSIONS"</code> */
    public final static String DEFAULT_SCOPE = "PERMISSIONS";

    /**
     * <p>Constructor for VkConfiguration.</p>
     */
    public VkConfiguration() {
        setScope(DEFAULT_SCOPE);
    }
}
