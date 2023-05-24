package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.ToString;
import org.pac4j.core.context.HttpConstants;

import java.io.Serial;

/**
 * A "See Other" HTTP action.
 *
 * @author Jerome Leleu
 * @since 4.0.0
 */
@Getter
@ToString(callSuper = true)
public class SeeOtherAction extends RedirectionAction implements WithLocationAction {

    @Serial
    private static final long serialVersionUID = 6749123580877847389L;
    private final String location;

    /**
     * <p>Constructor for SeeOtherAction.</p>
     *
     * @param location a {@link String} object
     */
    public SeeOtherAction(final String location) {
        super(HttpConstants.SEE_OTHER);
        this.location = location;
    }
}
