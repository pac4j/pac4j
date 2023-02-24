package org.pac4j.core.matching.matcher;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.CommonHelper;

import java.util.regex.Pattern;

/**
 * Matching on a HTTP header.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
@Getter
@Setter
@ToString
public class HeaderMatcher implements Matcher {

    private String headerName;

    private String expectedValue;

    protected Pattern pattern;

    /**
     * <p>Constructor for HeaderMatcher.</p>
     */
    public HeaderMatcher() {}

    /**
     * <p>Constructor for HeaderMatcher.</p>
     *
     * @param headerName a {@link java.lang.String} object
     * @param expectedValue a {@link java.lang.String} object
     */
    public HeaderMatcher(final String headerName, final String expectedValue) {
        setHeaderName(headerName);
        setExpectedValue(expectedValue);
    }

    /** {@inheritDoc} */
    @Override
    public boolean matches(final CallContext ctx) {
        CommonHelper.assertNotBlank("headerName", headerName);

        val headerValue = ctx.webContext().getRequestHeader(this.headerName);
        val headerNull = expectedValue == null && !headerValue.isPresent();
        val headerMatches = headerValue.isPresent() && pattern != null && pattern.matcher(headerValue.get()).matches();
        return headerNull || headerMatches;
    }

    /**
     * <p>Setter for the field <code>expectedValue</code>.</p>
     *
     * @param expectedValue a {@link java.lang.String} object
     */
    public void setExpectedValue(final String expectedValue) {
        this.expectedValue = expectedValue;
        if (expectedValue != null) {
            pattern = Pattern.compile(expectedValue);
        }
    }
}
