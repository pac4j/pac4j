package org.pac4j.core.profile;


/**
 * Denotes where an attribute is placed in a profile.
 * 
 * @author jkacer
 * @since 2.3.0
 */
public enum AttributeLocation {

    /** Profile "basic" attribute. */
    PROFILE_ATTRIBUTE,

    /** Profile authentication attribute. */
    AUTHENTICATION_ATTRIBUTE;
}
