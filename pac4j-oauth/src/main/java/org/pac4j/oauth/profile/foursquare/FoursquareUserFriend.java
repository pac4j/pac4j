package org.pac4j.oauth.profile.foursquare;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class represents a Foursquare user friend.
 *
 * @author Alexey Ogarkov
 * @since 1.5.0
 */
@Getter
@Setter
public class FoursquareUserFriend implements Serializable {

    @Serial
    private static final long serialVersionUID = 8954533489873703341L;

    private String id;
    private String firstName;
    private String lastName;
    private String gender;
    private String relationship;
    private String photo;
    private String bio;
    private String location;
    private String email;
}
