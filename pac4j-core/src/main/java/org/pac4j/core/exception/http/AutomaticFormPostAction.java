package org.pac4j.core.exception.http;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * This is an automatic form POST action.
 *
 * @author Jerome LELEU
 * @since 6.0.0
 */
@Getter
@ToString(callSuper = true)
public class AutomaticFormPostAction extends OkAction {

    private final String url;

    private final Map<String, String> data;

    public AutomaticFormPostAction(final String url, final Map<String, String> data, final String content) {
        super(content);
        this.url = url;
        this.data = data;
    }
}
