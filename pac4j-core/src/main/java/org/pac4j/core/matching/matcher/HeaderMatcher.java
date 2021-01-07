package org.pac4j.core.matching.matcher;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Matching on a HTTP header.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public class HeaderMatcher implements Matcher {

    private String headerName;

    private String expectedValue;

    protected Pattern pattern;

    public HeaderMatcher() {}

    public HeaderMatcher(final String headerName, final String expectedValue) {
        setHeaderName(headerName);
        setExpectedValue(expectedValue);
    }

    @Override
    public boolean matches(final WebContext context, final SessionStore sessionStore) {
        CommonHelper.assertNotBlank("headerName", headerName);

        final Optional<String> headerValue = context.getRequestHeader(this.headerName);
        final boolean headerNull = expectedValue == null && !headerValue.isPresent();
        final boolean headerMatches = headerValue.isPresent() && pattern != null && pattern.matcher(headerValue.get()).matches();
        return headerNull || headerMatches;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(final String headerName) {
        this.headerName = headerName;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(final String expectedValue) {
        this.expectedValue = expectedValue;
        if (expectedValue != null) {
            pattern = Pattern.compile(expectedValue);
        }
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "headerName", this.headerName, "expectedValue", this.expectedValue);
    }
}
