package org.pac4j.core.profile.converter;

import java.util.List;
import java.util.Objects;

/**
 * This is {@link ChainingConverter}.
 *
 * @author Misagh Moayyed
 * @since 6.4.0
 */
public class ChainingConverter implements AttributeConverter {
    private final List<AttributeConverter> converters;

    public ChainingConverter(final List<AttributeConverter> converters) {
        this.converters = converters;
    }

    @Override
    public Object convert(final Object o) {
        return converters.stream().map(c -> c.convert(o))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }
}
