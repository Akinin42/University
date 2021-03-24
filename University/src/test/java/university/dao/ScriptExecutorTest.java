package university.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import university.dao.impl.ConnectorPostgres;
import university.exceptions.DaoException;
import university.io.FileReader;

class ScriptExecutorTest {
    
    private static ScriptExecutor executor;
    private static Connector connector;
    
    @BeforeAll
    static void init() {
        connector = new ConnectorH2("h2");
        FileReader reader = new FileReader();
        executor = new ScriptExecutor(connector, reader);
    }

    @Test
    void executeScript_ShouldCreateTableInDatabase_WhenInputScriptCreateTable() {
        executor.executeScript("\\createtablescript.sql");
        boolean tableExists = false;
        try (Connection connection = connector.getConnection();
                ResultSet resultSet = connection.getMetaData().getTables(null, null, "GROUPS", null)){
            if(resultSet.next()) {
                tableExists = true;
            }
        }catch (SQLException e) {
            throw new DaoException("Execute query failed!", e);
        }
        assertTrue(tableExists);
    }
    
    @Test
    void executeScript_ShouldThrowDaoException_WhenConnectIsNot() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        Connector connector = new ConnectorPostgres("postgresdatabasewithoutuser", dataSource);
        FileReader reader = new FileReader();
        ScriptExecutor executor = new ScriptExecutor(connector, reader);
        assertThrows(DaoException.class, () -> executor.executeScript("\\createtablescript.sql"));
    }
    
    @Test
    void executeScript_ShouldThrowDaoException_WhenConnectNull() {
        FileReader reader = new FileReader();
        ScriptExecutor executor = new ScriptExecutor(null, reader);
        assertThrows(DaoException.class, () -> executor.executeScript("\\createtablescript.sql"));
    }    
}
