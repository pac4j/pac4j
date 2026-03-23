package org.pac4j.jee.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.pac4j.core.context.FrameworkParameters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * You should upgrade to the new <code>pac4j-jakartaee</code> module.
 *
 * Specific JEE parameters.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@RequiredArgsConstructor
@Getter
@Deprecated
public class JEEFrameworkParameters implements FrameworkParameters {

    private final HttpServletRequest request;

    private final HttpServletResponse response;
}
