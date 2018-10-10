package org.pac4j.core.state;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;

/**
 * State generator which returns a pre-defined value or a random one.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public class StaticOrRandomStateGenerator implements StateGenerator {

    private String stateData;

    public StaticOrRandomStateGenerator() {}

    public StaticOrRandomStateGenerator(final String stateData) {
        this.stateData = stateData;
    }

    @Override
    public String generateState(final WebContext webContext) {
        if (CommonHelper.isNotBlank(this.stateData)) {
            return this.stateData;
        } else {
            return CommonHelper.randomString(10);
        }
    }

    public String getStateData() {
        return stateData;
    }

    public void setStateData(final String stateData) {
        this.stateData = stateData;
    }
}
