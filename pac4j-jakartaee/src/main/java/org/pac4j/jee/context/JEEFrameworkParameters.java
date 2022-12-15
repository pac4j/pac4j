package org.pac4j.jee.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.context.FrameworkParameters;

/**
 * Specific JEE parameters.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@RequiredArgsConstructor
@Getter
public class JEEFrameworkParameters implements FrameworkParameters {

    private final HttpServletRequest request;

    private final HttpServletResponse response;
}
