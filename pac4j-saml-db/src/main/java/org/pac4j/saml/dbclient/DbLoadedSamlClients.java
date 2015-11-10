package org.pac4j.saml.dbclient;

import java.util.ArrayList;
import java.util.List;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.pac4j.saml.dbclient.dao.api.SamlClientDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * An alternative to {@link Clients}. Unlike {@link Clients}, this class works with clients whose definition is stored in a database. It is
 * limited to {@link DbLoadedSamlClient}s only, so clients of another type cannot be managed by this class.
 * </p>
 * 
 * <p>
 * This class is made to group multiple clients using a specific parameter to distinguish them, generally on one callback url.
 * </p>
 * 
 * <p>
 * The {@link #init()} method is used to initialize the callback urls of the clients from the callback url of the clients group if empty and
 * a specific parameter added to define the client targeted. It is implicitly called by the "finders" methods and doesn't need to be called
 * explicitly.
 * </p>
 * 
 * <p>
 * The {@link #findClient(WebContext)}, {@link #findClient(String)} or {@link #findClient(Class)} methods must be called to find the right
 * client according to the input context or type. The {@link #findAllClients()} method returns all the clients.
 * </p>
 *
 * TODO: We could have a common class (maybe abstract) that would contain common code of this class and {@link Clients}. Most code is
 * duplicated. {@link Clients} cannot be extended because it is final.
 * 
 * @author jkacer
 */
public class DbLoadedSamlClients extends InitializableWebObject {

	/** Default value for {@link #clientNameParameter}. */
    public final static String DEFAULT_CLIENT_NAME_PARAMETER = "client_name";

    /** SLF4J logger. */
    private final Logger logger = LoggerFactory.getLogger(DbLoadedSamlClients.class);
	
    /** Common callback URL, applied to clients that do not have any callback set. */
    private String callbackUrl;
    
    /** List of managed SAML clients. */
    private final List<DbLoadedSamlClient> clients;
    
    /** Name of the HTTP parameter holding client names. */
    private String clientNameParameter = DEFAULT_CLIENT_NAME_PARAMETER;
    
    /** DAO reading definitions of SAML clients from a database. */
    private SamlClientDao samlClientDao;

    
    // ------------------------------------------------------------------------------------------------------------------------------------
	
	
	/**
	 * Creates a new client grouping.
	 * 
	 * @param callbackUrl
	 *            Common Callback URL. Will be used by all clients, unless they have their own value.
	 */
	public DbLoadedSamlClients(final String callbackUrl) {
		super();
		this.callbackUrl = callbackUrl;
		this.clients = new ArrayList<DbLoadedSamlClient>();
	}
	
	
	/**
	 * Initializes all clients by loading them from the database.
	 * 
	 * @see org.pac4j.core.util.InitializableObject#internalInit()
	 */
	@Override
	protected void internalInit(final WebContext context) {

        CommonHelper.assertNotBlank("callbackUrl", this.callbackUrl);
        CommonHelper.assertNotNull("clients", this.clients);
        CommonHelper.assertNotNull("samlClientDao", this.samlClientDao);

        // Remove all clients first if re-initialized
        this.clients.clear();
        
        logger.debug("Loading SAML client definitions from the database...");
        List<DbLoadedSamlClientConfiguration> dbConfigurations = samlClientDao.loadAllClients();
        for (DbLoadedSamlClientConfiguration dbCfg: dbConfigurations) {
        	DbLoadedSamlClient client = new DbLoadedSamlClient(dbCfg);
        	this.clients.add(client);
        }
        logger.debug("SAML clients loaded OK.");
        
        for (final DbLoadedSamlClient client : this.clients) {
            String baseClientCallbackUrl = client.getCallbackUrl();
            // no callback url defined for the client -> set it with the group callback url
            if (baseClientCallbackUrl == null) {
                client.setCallbackUrl(this.callbackUrl);
                baseClientCallbackUrl = this.callbackUrl;
            }
            // if the "clientName" parameter is not already part of the callback url, add it unless the client has indicated to not include it.
        	if (client.isIncludeClientNameInCallbackUrl() && baseClientCallbackUrl.indexOf(this.clientNameParameter + "=") < 0) {
                client.setCallbackUrl(CommonHelper.addParameter(baseClientCallbackUrl, this.clientNameParameter, client.getName()));
            }
        }
	}

	
	// Copied from Clients
	public DbLoadedSamlClient findClient(final WebContext context) {
        final String name = context.getRequestParameter(this.clientNameParameter);
        CommonHelper.assertNotBlank("name", name);
        return findClient(context, name);
    }

    
	// Copied from Clients
    public DbLoadedSamlClient findClient(final WebContext context, final String name) {
        init(context);
        for (final DbLoadedSamlClient client : this.clients) {
            if (CommonHelper.areEquals(name, client.getName())) {
                return client;
            }
        }
        final String message = "No client found for name: " + name;
        logger.error(message);
        throw new TechnicalException(message);
    }
    
    
	// Copied from Clients
    public <C extends Client> DbLoadedSamlClient findClient(final WebContext context, final Class<C> clazz) {
		init(context);
		if (clazz != null) {
			for (final DbLoadedSamlClient client : this.clients) {
				if (clazz.isAssignableFrom(client.getClass())) {
					return client;
				}
			}
		}
        final String message = "No client found for class: " + clazz;
        logger.error(message);
        throw new TechnicalException(message);
    }    

    
	// Copied from Clients
    public List<DbLoadedSamlClient> findAllClients(final WebContext context) {
        init(context);
        return this.clients;
    }

    
	// Copied from Clients
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "clientTypeParameter", this.clientNameParameter, "clients", this.clients);
    }
    
    
    // ------------------------------------------------------------------------------------------------------------------------------------

    
    public String getClientNameParameter() {
        return this.clientNameParameter;
    }

    public void setClientNameParameter(final String clientNameParameter) {
        this.clientNameParameter = clientNameParameter;
    }

    public String getCallbackUrl() {
        return this.callbackUrl;
    }

    public void setCallbackUrl(final String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

	public SamlClientDao getSamlClientDao() {
		return samlClientDao;
	}

	public void setSamlClientDao(SamlClientDao samlClientDao) {
		this.samlClientDao = samlClientDao;
	}

}
