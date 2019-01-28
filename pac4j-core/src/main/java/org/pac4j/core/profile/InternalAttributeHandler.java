package org.pac4j.core.profile;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.JavaSerializationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Internally handles attributes (set / get).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class InternalAttributeHandler {

    protected transient final Logger logger = LoggerFactory.getLogger(getClass());

    public transient static final String PREFIX = "{#";
    public transient static final String PREFIX_BOOLEAN = PREFIX + "bool}";
    public transient static final String PREFIX_INT = PREFIX + "int}";
    public transient static final String PREFIX_LONG = PREFIX + "long}";
    public transient static final String PREFIX_DATE = PREFIX + "date}";
    public transient static final String PREFIX_URI = PREFIX + "uri}";
    public transient static final String PREFIX_SB64 = PREFIX + "sb64}";

    private JavaSerializationHelper serializationHelper = new JavaSerializationHelper();

    private boolean stringify = false;

    /**
     * Before saving the attribute into the attributes map.
     *
     * @param value the original value
     * @return the prepared value
     */
    public Object prepare(final Object value) {
        if (value == null || value instanceof String || !stringify) {
            return value;
        } else {
            if (value instanceof Boolean) {
                return PREFIX_BOOLEAN.concat(value.toString());
            } else if (value instanceof Integer) {
                return PREFIX_INT.concat(value.toString());
            } else if (value instanceof Long) {
                return PREFIX_LONG.concat(value.toString());
            } else if (value instanceof Date) {
                return PREFIX_DATE.concat(newSdf().format((Date) value));
            } else if (value instanceof URI) {
                return PREFIX_URI.concat(value.toString());
            } else {
                return PREFIX_SB64.concat(serializationHelper.serializeToBase64((Serializable) value));
            }
        }
    }

    /**
     * After retrieving the attribute from the attributes map.
     *
     * @param value the retrieved value
     * @return the restored value
     */
    public Object restore(final Object value) {
        if (value != null && value instanceof String) {
            final String sValue = (String) value;
            if (sValue.startsWith(PREFIX)) {
                if (sValue.startsWith(PREFIX_BOOLEAN)) {
                    return Boolean.parseBoolean(sValue.substring(PREFIX_BOOLEAN.length()));
                } else if (sValue.startsWith(PREFIX_INT)) {
                    return Integer.parseInt(sValue.substring(PREFIX_INT.length()));
                } else if (sValue.startsWith(PREFIX_LONG)) {
                    return Long.parseLong(sValue.substring(PREFIX_LONG.length()));
                } else if (sValue.startsWith(PREFIX_DATE)) {
                    final String d = sValue.substring(PREFIX_DATE.length());
                    try {
                        return newSdf().parse(d);
                    } catch (final ParseException e) {
                        logger.warn("Unable to parse stringified date: {}", d, e);
                    }
                } else if (sValue.startsWith(PREFIX_URI)) {
                    final String uri = sValue.substring(PREFIX_URI.length());
                    try {
                        return new URI(uri);
                    } catch (final URISyntaxException e) {
                        logger.warn("Unable to parse stringified URI: {}", uri, e);
                    }
                } else if (sValue.startsWith(PREFIX_SB64)) {
                    return serializationHelper.deserializeFromBase64(sValue.substring(PREFIX_SB64.length()));
                }
            }
        }
        return value;
    }

    protected SimpleDateFormat newSdf() {
        return new SimpleDateFormat(Converters.DATE_TZ_GENERAL_FORMAT);
    }

    public JavaSerializationHelper getSerializationHelper() {
        return serializationHelper;
    }

    public void setSerializationHelper(final JavaSerializationHelper serializationHelper) {
        this.serializationHelper = serializationHelper;
    }

    public boolean isStringify() {
        return stringify;
    }

    /**
     * Define if we need to turn all attributes into strings, to properly work with CAS
     * (regarding Kryo serialization or service ticket validation).
     *
     * @param stringify whether we need to turn all attributes into strings
     */
    public void setStringify(final boolean stringify) {
        this.stringify = stringify;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "serializationHelper", serializationHelper, "stringify", stringify);
    }
}
