package org.pac4j.oauth.profile.windowslive;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.Arrays;

/**
 * This class is the Windows Live profile definition.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveProfileDefinition extends CommonProfileDefinition<WindowsLiveProfile> {
    
    public static final String NAME = "name";
    public static final String LAST_NAME = "last_name";
    public static final String LINK = "link";
    public static final String UPDATED_TIME = "updated_time";
    
    public WindowsLiveProfileDefinition() {
        super(x -> new WindowsLiveProfile());
        Arrays.stream(new String[] {NAME, LAST_NAME}).forEach(a -> primary(a, Converters.STRING));
        primary(LINK, Converters.URL);
        primary(UPDATED_TIME, Converters.DATE_TZ_GENERAL);
    }
}
