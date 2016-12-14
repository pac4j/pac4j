package org.pac4j.oauth.client;

import java.util.Map;

import com.github.scribejava.core.model.Verb;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.scribe.builder.api.GenericApi20;

/**
 * <p>This class is a generic OAuth2 client to authenticate users in a standard OAuth2 server.</p>
 * <p>All configuration parameters can be specified setting the corresponding attribute.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.OAuth20Profile}.</p>
 * <p>This client requires next custom parameters in configuration:</p>
 * <ul>
 *   <li>authUrl : Server auth url</li>
 *   <li>tokeUrl : Server token url</li>
 *   <li>profileUrl : Server profile url</li>
 * <ul>
* <p>These additiona custom parameters may be used:</p>
 * <ul>
 *   <li>profilePath : Path to find profile in json response</li>
 *   <li>profileVerb : Http method used to request profile</li>
 *   <li>profileAttrs : Map of attribute name/json path for each profile attribute </li>
 * <ul>
 *
 * @author Julio Arrebola
 */
public class GenericOAuth20Client extends OAuth20Client<OAuth20Profile> {

    public GenericOAuth20Client() {
    }

    @Override
    protected void internalInit(final WebContext context) {       
        
        CommonHelper.assertNotNull("configuration", configuration);
        
        Object authUrl = configuration.getCustomParam("authUrl");
        CommonHelper.assertNotNull("authUrl", authUrl);
        
        Object tokenUrl = configuration.getCustomParam("tokenUrl");
        CommonHelper.assertNotNull("tokenUrl", tokenUrl);
        
        Object profileUrl = configuration.getCustomParam("profileUrl");
        CommonHelper.assertNotNull("profileUrl", profileUrl);
        
        GenericApi20 genApi = new GenericApi20(authUrl.toString(), tokenUrl.toString());
        configuration.setApi(genApi);
        
        Object profileVerb = configuration.getCustomParam("profileVerb");
        Object profilePath = configuration.getCustomParam("profilePath");
        Object profileAttrs = configuration.getCustomParam("profileAttrs");

        GenericOAuth20ProfileDefinition profileDefinition = new GenericOAuth20ProfileDefinition();
        profileDefinition.setProfileUrl(profileUrl.toString());
      
        if (profileVerb != null) {
            Verb auxVerb = profileVerb instanceof Verb?(Verb)profileVerb:
                    "POST".equalsIgnoreCase(profileVerb.toString())?Verb.POST:Verb.GET;
            profileDefinition.setProfileVerb(auxVerb);
        }

        if (profilePath != null) {
            profileDefinition.setFirstNodePath(profilePath.toString());
        }
        
        if (profileAttrs != null && profileAttrs instanceof Map) {
            for (Map.Entry<String,String> entry : ((Map<String,String>)profileAttrs).entrySet()) {
                profileDefinition.profileAttribute(entry.getKey(), entry.getValue(), null);
            }
        }

        configuration.setProfileDefinition(profileDefinition);
        setConfiguration(configuration);
        
        super.internalInit(context);
    }
}
