package org.pac4j.oidc.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringSerializer;
import com.nimbusds.oauth2.sdk.Scope;

/**
 * This class is a Kryo serializer for {@link Scope.Value}.
 * 
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class ScopeValueSerializer extends Serializer<Scope.Value> {

    private final StringSerializer stringSerializer = new StringSerializer();

    @Override
    public Scope.Value read(final Kryo kryo, final Input input, final Class<Scope.Value> aClass) {
        final String value = this.stringSerializer.read(kryo, input, String.class);
        final String requirement = this.stringSerializer.read(kryo, input, String.class);
        if (requirement == null) {
            return new Scope.Value(value);
        } else {
            return new Scope.Value(value, Scope.Value.Requirement.valueOf(requirement));
        }
    }

    @Override
    public void write(final Kryo kryo, final Output output, final Scope.Value value) {
        final String v = value.getValue();
        String r = null;
        final Scope.Value.Requirement requirement = value.getRequirement();
        if (requirement != null) {
            r = requirement.name();
        }
        this.stringSerializer.write(kryo, output, v);
        this.stringSerializer.write(kryo, output, r);
    }
}
