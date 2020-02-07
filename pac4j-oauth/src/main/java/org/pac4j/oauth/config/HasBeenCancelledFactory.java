package org.pac4j.oauth.config;

import org.pac4j.core.context.WebContext;

import java.util.function.Function;

/**
 * A factory to define if an OAuth login process has been cancelled.
 *
 * @author Jerome LELEU
 * @since 4.0.0
 */
public interface HasBeenCancelledFactory extends Function<WebContext, Boolean> {
}
