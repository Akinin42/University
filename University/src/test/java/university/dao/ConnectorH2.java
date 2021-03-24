package university.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import org.h2.jdbcx.JdbcDataSource;
import university.exceptions.DaoException;

public class ConnectorH2 implements Connector {

    private static final String PROPERTY_URL = "url";
    private static final String PROPERTY_USERNAME = "username";
    private static final String PROPERTY_PASSWORD = "password";

    private final JdbcDataSource dataSource;
    private final String user;
    private final String password;
    private final String url;

    public ConnectorH2(String propertiesPath) {
        ResourceBundle resources = ResourceBundle.getBundle(propertiesPath);
        this.url = resources.getString(PROPERTY_URL);
        this.user = resources.getString(PROPERTY_USERNAME);
        this.password = resources.getString(PROPERTY_PASSWORD);
        dataSource = new JdbcDataSource();
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
