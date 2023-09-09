package org.pac4j.oidc.credentials;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializerBase;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import net.minidev.json.JSONObject;
import org.pac4j.core.credentials.Credentials;

import java.io.IOException;
import java.io.Serial;

/**
 * Credentials containing the authorization code sent by the OpenID Connect server.
 *
 * @author Michael Remond
 * @author Jerome Leleu
 * @since 1.7.0
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class OidcCredentials extends Credentials {

    @Serial
    private static final long serialVersionUID = 6772331801527223938L;

    @EqualsAndHashCode.Include
    @JsonSerialize(using = ToStringSerializer.class)
    @JsonDeserialize(using = AuthorizationCodeDeserializer.class)
    private AuthorizationCode code;

    @JsonSerialize(using = AccessTokenSerializer.class)
    @JsonDeserialize(using = AccessTokenDeserializer.class)
    private AccessToken accessToken;

    @JsonSerialize(using = RefreshTokenSerializer.class)
    @JsonDeserialize(using = RefreshTokenDeserializer.class)
    private RefreshToken refreshToken;

    @JsonSerialize(using = JWTSerializer.class)
    @JsonDeserialize(using = JWTDeserializer.class)
    private JWT idToken;

    static class JWTSerializer extends ToStringSerializerBase {

        public JWTSerializer() {
            super(JWT.class);
        }

        @Override
        public String valueToString(final Object value) {
            return ((JWT) value).serialize();
        }

        @Override
        public void serializeWithType(final Object value, JsonGenerator jgen,
                                      SerializerProvider provider, TypeSerializer typeSer) throws IOException {
            val typeId = typeSer.typeId(value, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(jgen, typeId);
            jgen.writeStringField("value", ((JWT) value).serialize());
            typeId.wrapperWritten = !jgen.canWriteTypeId();
            typeSer.writeTypeSuffix(jgen, typeId);
        }
    }

    static class RefreshTokenSerializer extends StdSerializer<RefreshToken> {
        public RefreshTokenSerializer() {
            this(null);
        }

        public RefreshTokenSerializer(final Class<RefreshToken> t) {
            super(t);
        }

        @Override
        public void serialize(final RefreshToken refreshToken, final JsonGenerator jsonGenerator,
                              final SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(refreshToken.toJSONObject());
        }

        @Override
        public void serializeWithType(final RefreshToken accessToken,
                                      final JsonGenerator jgen,
                                      final SerializerProvider provider,
                                      final TypeSerializer typeSer) throws IOException {
            val typeId = typeSer.typeId(accessToken, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(jgen, typeId);
            accessToken.toJSONObject().forEach((name, value) -> {
                try {
                    jgen.writeObjectField(name, value);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            });
            typeId.wrapperWritten = !jgen.canWriteTypeId();
            typeSer.writeTypeSuffix(jgen, typeId);
        }
    }

    static class AccessTokenSerializer extends StdSerializer<AccessToken> {
        public AccessTokenSerializer() {
            this(null);
        }

        public AccessTokenSerializer(final Class<AccessToken> t) {
            super(t);
        }

        @Override
        public void serialize(final AccessToken accessToken, final JsonGenerator jsonGenerator,
                              final SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(accessToken.toJSONObject());
        }

        @Override
        public void serializeWithType(final AccessToken accessToken,
                                      final JsonGenerator jgen,
                                      final SerializerProvider provider,
                                      final TypeSerializer typeSer) throws IOException {
            val typeId = typeSer.typeId(accessToken, JsonToken.START_OBJECT);
            typeSer.writeTypePrefix(jgen, typeId);
            accessToken.toJSONObject().forEach((name, value) -> {
                try {
                    jgen.writeObjectField(name, value);
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            });
            typeId.wrapperWritten = !jgen.canWriteTypeId();
            typeSer.writeTypeSuffix(jgen, typeId);
        }
    }


    static class AuthorizationCodeDeserializer extends StdDeserializer<AuthorizationCode> {

        protected AuthorizationCodeDeserializer() {
            super(AuthorizationCode.class);
        }

        protected AuthorizationCodeDeserializer(final JavaType valueType) {
            super(valueType);
        }

        @Override
        public AuthorizationCode deserialize(final JsonParser jsonParser,
                                             final DeserializationContext deserializationContext) throws IOException {
            val node = (JsonNode) jsonParser.getCodec().readTree(jsonParser);
            return new AuthorizationCode(node.asText());
        }
    }

    static class RefreshTokenDeserializer extends StdDeserializer<RefreshToken> {

        protected RefreshTokenDeserializer() {
            super(AuthorizationCode.class);
        }

        protected RefreshTokenDeserializer(final JavaType valueType) {
            super(valueType);
        }

        @Override
        public RefreshToken deserialize(final JsonParser jsonParser,
                                        final DeserializationContext deserializationContext) throws IOException {
            final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            return new RefreshToken(node.asText());
        }
    }

    static class JWTDeserializer extends StdDeserializer<JWT> {

        protected JWTDeserializer() {
            super(JWT.class);
        }

        protected JWTDeserializer(final JavaType valueType) {
            super(valueType);
        }

        @Override
        public JWT deserialize(final JsonParser jsonParser,
                               final DeserializationContext deserializationContext) throws IOException {
            try {
                final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
                return JWTParser.parse(node.asText());
            } catch (final Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public Object deserializeWithType(final JsonParser jp, final DeserializationContext ctxt,
                                          final TypeDeserializer typeDeserializer) throws IOException {
            try {
                val node = (JsonNode) jp.getCodec().readTree(jp);
                val objectMapper = new ObjectMapper();
                val jsonObject = objectMapper.readValue(node.get(1).toString(), JSONObject.class);
                return JWTParser.parse(jsonObject.get("value").toString());
            } catch (final Exception e) {
                throw new IOException(e);
            }
        }
    }

    static class AccessTokenDeserializer extends StdDeserializer<AccessToken> {

        protected AccessTokenDeserializer() {
            super(AccessToken.class);
        }

        protected AccessTokenDeserializer(final JavaType valueType) {
            super(valueType);
        }

        @Override
        public AccessToken deserialize(final JsonParser jsonParser,
                                       final DeserializationContext deserializationContext) throws IOException {
            try {
                val objectMapper = new ObjectMapper();
                val jsonObject = objectMapper.readValue(jsonParser, JSONObject.class);
                return AccessToken.parse(jsonObject);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object deserializeWithType(final JsonParser jsonParser, final DeserializationContext deserializationContext,
                                          final TypeDeserializer typeDeserializer) throws IOException, JacksonException {
            try {
                val node = (JsonNode) jsonParser.getCodec().readTree(jsonParser);
                val objectMapper = new ObjectMapper();
                val jsonObject = objectMapper.readValue(node.get(1).toString(), JSONObject.class);
                return AccessToken.parse(jsonObject);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
