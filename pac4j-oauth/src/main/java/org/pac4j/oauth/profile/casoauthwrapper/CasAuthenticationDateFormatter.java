package org.pac4j.oauth.profile.casoauthwrapper;

import org.pac4j.core.profile.converter.DateConverter;

/**
 * Date formatter for the CAS authentication date.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasAuthenticationDateFormatter extends DateConverter {

    /**
     * <p>Constructor for CasAuthenticationDateFormatter.</p>
     */
    public CasAuthenticationDateFormatter() {
        super("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    /** {@inheritDoc} */
    @Override
    public Object convert(final Object attribute) {
        var a = attribute;
        if (a instanceof String s) {
            var pos = s.lastIndexOf("[");
            if (pos > 0) {
                s = s.substring(0, pos);
                pos = s.lastIndexOf(":");
                if (pos > 0) {
                    s = s.substring(0, pos) + s.substring(pos + 1, s.length());
                }
                a = s;
            }
        }
        return super.convert(a);
    }
}
