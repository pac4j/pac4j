package org.pac4j.core.profile.converter;

import org.pac4j.core.util.CommonHelper;

/**
 * This class makes replacements in a String.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringReplaceConverter implements AttributeConverter<String> {
    
    private final String regex;
    
    private final String replacement;
    
    public StringReplaceConverter(final String regex, final String replacement) {
        this.regex = regex;
        this.replacement = replacement;
    }
    
    @Override
    public String convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            final String s = (String) attribute;
            if (CommonHelper.isNotBlank(s) && CommonHelper.isNotBlank(this.regex)
                && CommonHelper.isNotBlank(this.replacement)) {
                return s.replaceAll(this.regex, this.replacement);
            }
        }
        return null;
    }
}
