package university.dao.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.postgresql.ds.common.BaseDataSource;
import university.dao.Connector;
import university.exceptions.DaoException;

public class ConnectorPostgres implements Connector {

    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    private final BaseDataSource dataSource;
    private final String user;
    private final String password;
    private final String url;

    public ConnectorPostgres(String propertiesPath, BaseDataSource dataSource) {
        try {
            ResourceBundle resources = ResourceBundle.getBundle(propertiesPath);
            this.url = resources.getString(PROPERTY_URL);
            this.user = resources.getString(PROPERTY_USERNAME);
            this.password = resources.getString(PROPERTY_PASSWORD);
        } catch (MissingResourceException | NullPointerException e) {
            throw new IllegalArgumentException("This properties path not exists!" + propertiesPath, e);
        }
        this.dataSource = dataSource;
        dataSource.setURL(url);
        dataSource.setUser(user);
        dataSource.setPassword(password);
    }

    @Override
    public Connection getConnection() throws DaoException {
        try {
            return dataSource.getConnection(user, password);
        } catch (SQLException e) {
            throw new DaoException("There isn't database connect to " + url, e);
        }
    }
}
