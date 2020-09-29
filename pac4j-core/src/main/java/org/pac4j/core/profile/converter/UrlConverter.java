package org.pac4j.core.profile.converter;

import org.pac4j.core.util.CommonHelper;

import java.net.URI;

/**
 * URL converter.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class UrlConverter extends AbstractAttributeConverter {

    public UrlConverter() {
        super(URI.class);
    }

    @Override
    protected URI internalConvert(final Object attribute) {
        if (attribute instanceof String) {
            final String s = ((String) attribute).replaceAll("\\/", "/");
            return CommonHelper.asURI(s);
        }
        return null;
    }
}
