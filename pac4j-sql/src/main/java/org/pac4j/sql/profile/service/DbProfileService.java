package org.pac4j.sql.profile.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.service.AbstractProfileService;
import org.pac4j.core.util.serializer.JsonSerializer;
import org.pac4j.sql.profile.DbProfile;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * The DB profile service (which supersedes the DB authenticator).
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
@Getter
@ToString(callSuper = true)
public class DbProfileService extends AbstractProfileService<DbProfile> {

    protected DBI dbi;

    private String usersTable = "users";

    @Setter
    private DataSource dataSource;

    public DbProfileService() {}

    public DbProfileService(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DbProfileService(final DataSource dataSource, final String attributes) {
        this.dataSource = dataSource;
        setAttributes(attributes);
    }

    public DbProfileService(final DataSource dataSource, final String attributes, final PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        setAttributes(attributes);
        setPasswordEncoder(passwordEncoder);
    }

    public DbProfileService(final DataSource dataSource, final PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        setPasswordEncoder(passwordEncoder);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotNull("passwordEncoder", getPasswordEncoder());
        assertNotNull("dataSource", this.dataSource);
        this.dbi = new DBI(this.dataSource);

        defaultProfileDefinition(new CommonProfileDefinition(x -> new DbProfile()));
        setSerializer(new JsonSerializer(DbProfile.class));

        super.internalInit(forceReinit);
    }

    @Override
    protected void insert(final Map<String, Object> attributes) {
        final List<String> names = new ArrayList<>();
        final List<String> questionMarks = new ArrayList<>();
        final List<Object> values = new ArrayList<>();
        for (val entry : attributes.entrySet()) {
            names.add(entry.getKey());
            questionMarks.add("?");
            values.add(entry.getValue());
        }

        val query = "insert into " + usersTable + " (" + buildAttributesList(names) + ") values ("
            + buildAttributesList(questionMarks) + ")";
        execute(query, values.toArray());
    }

    @Override
    protected void update(final Map<String, Object> attributes) {
        val attributesList = new StringBuilder();
        String id = null;
        final List<Object> values = new ArrayList<>();
        var i = 0;
        for (val entry : attributes.entrySet()) {
            val name = entry.getKey();
            val value = entry.getValue();
            if (ID.equals(name)) {
                id = (String) value;
            } else {
                if (i > 0) {
                    attributesList.append(",");
                }
                attributesList.append(name);
                attributesList.append("= :");
                attributesList.append(name);
                values.add(value);
                i++;
            }
        }

        assertNotNull(ID, id);
        values.add(id);
        val query = "update " + usersTable + " set " + attributesList.toString() + " where " + getIdAttribute() + " = :id";
        execute(query, values.toArray());
    }

    @Override
    protected void deleteById(final String id) {
        val query = "delete from " + usersTable + " where " + getIdAttribute() + " = :id";
        execute(query, id);
    }

    protected void execute(final String query, final Object... args) {
        Handle h = null;
        try {
            h = dbi.open();
            logger.debug("Execute query: {} and values: {}", query, args);
            h.execute(query, args);
        } finally {
            if (h != null) {
                h.close();
            }
        }
    }

    @Override
    protected List<Map<String, Object>> read(final List<String> names, final String key, final String value) {
        val attributesList = buildAttributesList(names);

        val query = "select " + attributesList + " from " + usersTable + " where " + key + " = :" + key;
        return query(query, key, value);
    }

    protected List<Map<String, Object>> query(final String query, final String key, final String value) {
        Handle h = null;
        try {
            h = dbi.open();
            logger.debug("Query: {} for key/value: {} / {}", query, key, value);
            return h.createQuery(query).bind(key, value).list(2);
        } finally {
            if (h != null) {
                h.close();
            }
        }
    }

    protected String buildAttributesList(final List<String> names) {
        val sb = new StringBuilder();
        var firstOne = true;
        for (val name : names) {
            if (!firstOne) {
                sb.append(",");
            }
            sb.append(name);
            firstOne = false;
        }
        return sb.toString();
    }

    public void setUsersTable(final String usersTable) {
        assertNotBlank("usersTable", usersTable);
        this.usersTable = usersTable;
    }
}
