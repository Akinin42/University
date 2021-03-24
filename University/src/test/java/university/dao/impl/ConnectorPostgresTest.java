package university.dao.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;

import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import university.dao.Connector;
import university.exceptions.DaoException;

class ConnectorPostgresTest {

    private Connector connector;
    private PGSimpleDataSource dataSourceMock;

    @BeforeEach
    void init() {
        dataSourceMock = mock(PGSimpleDataSource.class);
        connector = new ConnectorPostgres("postgresdatabase", dataSourceMock);
    }

    @Test
    void getConnection_ShouldSummonDataSourceGetConnection() throws SQLException {
        connector.getConnection();
        verify(dataSourceMock).setURL(anyString());
        verify(dataSourceMock).setUser(anyString());
        verify(dataSourceMock).setPassword(anyString());
        verify(dataSourceMock).getConnection(anyString(), anyString());
    }

    @Test
    void connectorPostgres_ShouldThrowIllegalArgumentException_WhenPropertyFileNotExists() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConnectorPostgres("notExistPropertiesPart", dataSourceMock));
    }

    @Test
    void connectorPostgres_ShouldThrowIllegalArgumentException_WhenDataSourceNull() {
        assertThrows(IllegalArgumentException.class, () -> new ConnectorPostgres("notExistPropertiesPart", null));
    }

    @Test
    void getConnection_ShouldThrowDaoException_WhenUserIsNotInProperty() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Connector connector = new ConnectorPostgres("postgresdatabasewithoutuser", dataSource);
        assertThrows(DaoException.class, () -> connector.getConnection());
    }
}
