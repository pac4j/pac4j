package org.pac4j.oidc.metadata.chain;

import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityType;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityMetadataValidator;
import com.nimbusds.openid.connect.sdk.federation.trust.EntityStatementRetriever;
import com.nimbusds.openid.connect.sdk.federation.trust.InvalidEntityMetadataException;
import com.nimbusds.openid.connect.sdk.federation.trust.ResolveException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minidev.json.JSONObject;

/**
 * Validate entity metadata in federation.
 *
 * @author Jerome LELEU
 * @since 6.4.0
 */
@RequiredArgsConstructor
public class FederationEntityMetadataValidator implements EntityMetadataValidator {

    private final EntityStatementRetriever statementRetriever;

    @Override
    public EntityType getType() {
        return EntityType.OPENID_PROVIDER;
    }

    @Override
    public void validate(final EntityID entityID, final JSONObject metadata) throws InvalidEntityMetadataException {
        EntityStatement targetStatement;
        try {
            targetStatement = statementRetriever.fetchEntityConfiguration(entityID);
        } catch (final ResolveException e) {
            throw new InvalidEntityMetadataException("Cannot fetch entity statement: " + e.getMessage());
        }
        val sub = targetStatement.getClaimsSet().getSubject();
        if (sub == null || !entityID.toSubject().equals(sub)) {
            throw new InvalidEntityMetadataException("Bad subject in entity statement: " + sub);
        }
    }
}
