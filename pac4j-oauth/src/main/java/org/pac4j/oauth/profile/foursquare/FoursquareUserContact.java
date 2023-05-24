package org.pac4j.oauth.profile.foursquare;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>This class is the user profile for Foursquare with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.FoursquareClient}.</p>
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
@Getter
@Setter
public class FoursquareUserContact implements Serializable {

    @Serial
    private static final long serialVersionUID = -4866834192367416908L;

    private String email;
    private String twitter;
    private String facebook;
}
