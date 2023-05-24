package org.pac4j.mongo.profile;

import lombok.ToString;
import org.pac4j.core.profile.CommonProfile;

import java.io.Serial;

/**
 * <p>The user profile returned from a MongoDB.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@ToString(callSuper = true)
public class MongoProfile extends CommonProfile {

    @Serial
    private static final long serialVersionUID = 7289249610131900281L;
}
