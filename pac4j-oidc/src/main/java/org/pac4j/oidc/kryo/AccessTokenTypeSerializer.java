package org.pac4j.oidc.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;

/**
 * This class is a Kryo serializer for {@link AccessTokenType}.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public class AccessTokenTypeSerializer extends Serializer<AccessTokenType> {

    private final StringSerializer stringSerializer = new StringSerializer();

    @Override
    public AccessTokenType read(Kryo kryo, Input input, Class<AccessTokenType> aClass) {
        final String value = this.stringSerializer.read(kryo, input, String.class);
        return new AccessTokenType(value);
    }

    @Override
    public void write(Kryo kryo, Output output, AccessTokenType accessTokenType) {
        this.stringSerializer.write(kryo, output, accessTokenType.getValue());
    }
}
