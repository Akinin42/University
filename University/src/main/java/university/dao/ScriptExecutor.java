package university.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import university.exceptions.DaoException;
import university.io.FileReader;

public class ScriptExecutor {

    private final Connector connector;
    private final FileReader reader;

    public ScriptExecutor(Connector connector,FileReader reader) {
        this.connector = connector;
        this.reader = reader;
    }

    public void executeScript(String scriptPath) {        
        String queryCreateDB = createSqlQuery(reader.read(scriptPath));
        try (Connection connection = connector.getConnection();
                PreparedStatement statement = connection.prepareStatement(queryCreateDB)) {
            statement.execute();
        } catch (SQLException | NullPointerException e) {
            throw new DaoException("Unable to execute schema script", e);
        }
    }

    private String createSqlQuery(List<String> scriptFileContent) {
        return scriptFileContent.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
